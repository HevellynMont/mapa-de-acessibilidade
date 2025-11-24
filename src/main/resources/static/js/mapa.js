document.addEventListener('DOMContentLoaded', () => {

    const mapa = L.map('mapa', { zoomControl: false }).setView([-20.3155, -40.3128], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OSM' }).addTo(mapa);
    L.control.zoom({ position: 'topright' }).addTo(mapa);

    function criarIcone(cor) {
        return L.icon({
            iconUrl: `https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-${cor}.png`,
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });
    }

    

    let todosMarcadores = [];
    let localAtualId = null;
    const SEAL_THRESHOLD = 0.6; // 60%
    const MIN_COMMENTS_FOR_SEAL = 8;
    // Quantas opções de tag existem (usado para calcular nota a partir das tags)
    const TOTAL_TAG_OPTIONS = 8;

    const userRole = localStorage.getItem('userRole');
    const btnAddLocal = document.getElementById('btn-zoom-out');
    const ratingSection = document.querySelector('.user-rating-section');

    if (userRole !== 'proprietario') {
        if (btnAddLocal) btnAddLocal.style.display = 'none';
    }

    if (userRole === 'proprietario') {
        if (ratingSection) ratingSection.style.display = 'none';
    }

    // Preencher avatar na seção 'Avalie sua experiência'
    (function renderRatingAvatar(){
        const avatarContainer = document.querySelector('.user-avatar-icon');
        if (!avatarContainer) return;
        const storedAvatar = localStorage.getItem('userAvatar');
        if (storedAvatar) {
            avatarContainer.innerHTML = `<img src="${storedAvatar}" alt="Seu avatar" class="comment-avatar-img">`;
        } else {
            avatarContainer.innerHTML = `<i class="fa-solid fa-circle-user"></i>`;
        }
    })();

    // Função para carregar locais do backend
    async function carregarPontos() {
        try {
            const response = await fetch('http://localhost:8080/api/locais');
            if (!response.ok) {
                console.error('Erro ao buscar locais:', response.status);
                return;
            }
            
            const locais = await response.json();
            console.log('Locais carregados do backend:', locais);
            
            // Remove marcadores anteriores
            todosMarcadores.forEach(item => mapa.removeLayer(item.marker));
            todosMarcadores = [];

            // Adiciona marcadores para cada local
            locais.forEach(local => {
                if (local.latitude && local.longitude) {
                    const hasSeal = temSelo(local);
                    const iconColor = hasSeal ? 'green' : 'grey';
                    const icon = criarIcone(iconColor);

                    const marcador = L.marker([local.latitude, local.longitude], { icon: icon });
                    marcador.addTo(mapa);

                    marcador.on('click', () => {
                        abrirPainel(local);
                        mapa.setView([local.latitude, local.longitude], 16);
                    });

                    todosMarcadores.push({ local, marker: marcador });
                }
            });
        } catch (error) {
            console.error('Erro ao carregar locais:', error);
        }
    }

    carregarPontos();

    const painel = document.getElementById('panel-detalhes');
    const botaoFecharPainel = document.getElementById('panel-close');

    async function abrirPainel(local) {
        localAtualId = local.id;
        document.getElementById('panel-titulo').textContent = local.nome;
        
        const descEl = document.getElementById('panel-descricao');
        if(descEl) {
            descEl.textContent = local.descricao;
            descEl.style.display = 'block';
        }

        const imgEl = document.getElementById('panel-imagem-principal');
        const placeholderEl = document.getElementById('panel-imagem-placeholder');
        
        if (local.foto) {
            imgEl.src = local.foto;
            imgEl.style.display = 'block';
            if (placeholderEl) placeholderEl.style.display = 'none';
        } else {
            imgEl.style.display = 'none';
            if (placeholderEl) placeholderEl.style.display = 'flex';
        }

        const panelComentarios = document.getElementById('panel-comentarios');
        panelComentarios.innerHTML = '';

        // Buscar comentários do backend
        try {
            const response = await fetch(`http://localhost:8080/api/comentarios/local/${local.id}`);
            let comentarios = [];
            
            if (response.ok) {
                comentarios = await response.json();
            } else if (response.status !== 204) {
                console.error('Erro ao buscar comentários:', response.status);
            }

            if (comentarios && comentarios.length > 0) {
                // Avaliar selo com base nos comentários
                local.comentarios = comentarios;
                avaliarSelo(local);

                // Calcula e mostra a média de avaliação baseada nas tags dos comentários
                const panelNotaEl = document.getElementById('panel-nota');
                if (panelNotaEl) {
                    const avg = calcularNotaAcessibilidade(local);
                    panelNotaEl.innerHTML = renderStarsHtml(avg);
                    panelNotaEl.style.display = 'block';
                }

                // Renderiza os comentários
                comentarios.forEach((c, idx) => {
                    const tagNames = c.tags ? c.tags.map(t => t.nome) : [];
                    const autorNome = c.pessoa ? c.pessoa.nome : 'Anônimo';
                    const pessoaId = c.pessoa ? c.pessoa.id : null;
                    const textoComentario = c.descricao || c.texto || '';
                    adicionarComentarioNaLista(autorNome, textoComentario, tagNames, c.id, pessoaId, false, null);
                });
            } else {
                avaliarSelo(local);
                const panelNotaEl = document.getElementById('panel-nota');
                if (panelNotaEl) panelNotaEl.style.display = 'none';
                panelComentarios.innerHTML = '<p class="empty-state">Nenhum comentário ainda.</p>';
            }
        } catch (error) {
            console.error('Erro ao buscar comentários:', error);
            panelComentarios.innerHTML = '<p class="empty-state">Erro ao carregar comentários.</p>';
        }

        painel.classList.add('is-open');
        document.body.style.setProperty('--panel-width', '400px');
    }

    botaoFecharPainel.addEventListener('click', () => {
        painel.classList.remove('is-open');
        document.body.style.setProperty('--panel-width', '0px');
        localAtualId = null;
    });

    const modal = document.getElementById('modal-novo-local');
    const formNovoLocal = document.getElementById('form-novo-local');
    const modalCloseBtn = document.getElementById('close-modal');
    const btnPerfil = document.getElementById('btn-zoom-in');

    if (btnAddLocal) {
        btnAddLocal.addEventListener('click', () => {
            modal.style.display = 'flex';
        });
    }

if (btnPerfil) {
        btnPerfil.addEventListener('click', () => {
            const role = localStorage.getItem('userRole');
            if (role === 'proprietario') {
                window.location.href = 'perfil-prop.html';
            } else {
                window.location.href = 'perfil-user.html';
            }
        });
    }

    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    if (formNovoLocal) {
        formNovoLocal.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const userId = localStorage.getItem('userId');
            if (!userId) { alert("Faça login."); window.location.href='login.html'; return; }

            const nome = document.getElementById('local-nome').value;
            const endereco = document.getElementById('local-endereco').value;
            const descricao = document.getElementById('local-descricao').value;
            const btnSalvar = formNovoLocal.querySelector('.btn-salvar');

            btnSalvar.textContent = "Buscando endereço...";
            btnSalvar.disabled = true;

            try {
                const query = `${endereco}`;
                const geoResponse = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`);
                const geoData = await geoResponse.json();

                let lat, lon;

                if (geoData && geoData.length > 0) {
                    lat = parseFloat(geoData[0].lat);
                    lon = parseFloat(geoData[0].lon);
                } else {
                    const center = mapa.getCenter();
                    lat = center.lat;
                    lon = center.lng;
                    alert("Endereço não encontrado exato. Usando centro da tela.");
                }

                btnSalvar.textContent = "Salvando...";

                // Enviar para o backend
                const novoLocal = {
                    nome: nome,
                    endereco: endereco,
                    descricao: descricao,
                    latitude: lat,
                    longitude: lon
                };

                const response = await fetch(`http://localhost:8080/api/locais?proprietarioId=${userId}&tagIds=`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(novoLocal)
                });

                if (response.ok) {
                    const localSalvo = await response.json();
                    alert("Local salvo com sucesso!");
                    modal.style.display = 'none';
                    formNovoLocal.reset();
                    
                    await carregarPontos();
                    mapa.setView([lat, lon], 16);
                } else {
                    alert("Erro ao salvar local no servidor.");
                }

            } catch (error) {
                console.error(error);
                alert("Erro ao processar requisição.");
            } finally {
                btnSalvar.textContent = "Salvar Local";
                btnSalvar.disabled = false;
            }
        });
    }

    const searchInput = document.querySelector('.search-input');
    const searchBtn = document.querySelector('.search-btn');

    // Cria container de sugestões abaixo da search-bar
    const searchBar = document.querySelector('.search-bar-container');
    let suggestionsEl = null;
    if (searchBar) {
        suggestionsEl = document.createElement('div');
        suggestionsEl.className = 'search-suggestions';
        suggestionsEl.style.display = 'none';
        searchBar.appendChild(suggestionsEl);
    }

    // Normaliza strings removendo acentos e transformando em lowercase
    function normalizeForSearch(s) {
        return (s || '').toString().normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
    }

    // Atualiza a lista de sugestões com base no termo
    async function updateSuggestions(term) {
        if (!suggestionsEl) return;
        const raw = (term || '').trim();
        if (!raw) { suggestionsEl.innerHTML = ''; suggestionsEl.style.display = 'none'; return; }

        const termo = normalizeForSearch(raw);
        
        try {
            const response = await fetch('http://localhost:8080/api/locais');
            if (!response.ok) return;
            
            const locais = await response.json();
            const matches = locais.filter(l => {
                const nome = normalizeForSearch(l.nome);
                const endereco = normalizeForSearch(l.endereco);
                return (nome && nome.includes(termo)) || (endereco && endereco.includes(termo));
            }).slice(0, 8);

            suggestionsEl.innerHTML = '';
            if (matches.length === 0) {
                const no = document.createElement('div');
                no.className = 'search-no-results';
                no.textContent = 'Nenhum resultado encontrado';
                suggestionsEl.appendChild(no);
                suggestionsEl.style.display = 'block';
                return;
            }

            matches.forEach(m => {
                const item = document.createElement('div');
                item.className = 'search-suggestion';
                const title = document.createElement('div');
                title.className = 'sug-title';
                title.textContent = m.nome || 'Local sem nome';
                const sub = document.createElement('div');
                sub.className = 'sug-sub';
                sub.textContent = m.endereco || '';
                item.appendChild(title);
                item.appendChild(sub);
                item.addEventListener('click', () => {
                    abrirPainel(m);
                    if (m.latitude && m.longitude) mapa.setView([m.latitude, m.longitude], 16);
                    suggestionsEl.innerHTML = '';
                    suggestionsEl.style.display = 'none';
                });
                suggestionsEl.appendChild(item);
            });
            suggestionsEl.style.display = 'block';
        } catch (error) {
            console.error('Erro ao buscar locais:', error);
        }
    }

    // Fechar sugestões ao clicar fora
    document.addEventListener('click', (e) => {
        if (!searchBar) return;
        if (!searchBar.contains(e.target)) {
            if (suggestionsEl) suggestionsEl.style.display = 'none';
        }
    });

    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            updateSuggestions(e.target.value);
        });
        searchInput.addEventListener('focus', (e) => updateSuggestions(e.target.value));
    }

    function realizarPesquisa() {
        const termo = searchInput.value.trim().toLowerCase();
        if (!termo) return;

        const encontrado = todosMarcadores.find(item => item.local.nome.toLowerCase().includes(termo));

        if (encontrado) {
            abrirPainel(encontrado.local);
            mapa.setView([encontrado.local.latitude, encontrado.local.longitude], 16);
        } else {
            alert("Local não encontrado.");
        }
    }

    if(searchBtn) {
        searchBtn.addEventListener('click', realizarPesquisa);
        searchInput.addEventListener('keypress', (e) => {
            if(e.key === 'Enter') realizarPesquisa();
        });
    }

    const handle = document.querySelector('.resize-handle');
    let isResizing = false;

    if (handle) {
        handle.addEventListener('mousedown', (e) => {
            e.preventDefault();
            isResizing = true;
            document.body.style.cursor = 'col-resize';
            document.body.classList.add('resizing');
        });
        document.addEventListener('mousemove', (e) => {
            if (!isResizing) return;
            let newWidth = e.clientX;
            if (newWidth < 300) newWidth = 300;
            if (newWidth > 800) newWidth = 800;
            painel.style.width = `${newWidth}px`;
            document.body.style.setProperty('--panel-width', `${newWidth}px`);
        });
        document.addEventListener('mouseup', () => {
            isResizing = false;
            document.body.style.cursor = 'default';
            document.body.classList.remove('resizing');
        });
    }

    const tagsChips = document.querySelectorAll('.tag-chip');
    let tagIdsSelecionadas = []; 

    tagsChips.forEach(chip => {
        chip.addEventListener('click', function() {
            this.classList.toggle('selected');
            const tagId = this.getAttribute('data-id'); // ✅ CORRIGIDO: Pega o ID numérico
            
            if (this.classList.contains('selected')) {
                tagIdsSelecionadas.push(tagId);
            } else {
                tagIdsSelecionadas = tagIdsSelecionadas.filter(t => t !== tagId);
            }
        });
    });

    const btnEnviarComentario = document.getElementById('btn-enviar-avaliacao');
    const textoComentarioInput = document.getElementById('novo-comentario-texto');

    if (btnEnviarComentario) {
        btnEnviarComentario.addEventListener('click', async () => {
            const texto = textoComentarioInput.value;
            if (texto.trim() === "") { alert("Escreva algo!"); return; }
            
            const userId = localStorage.getItem('userId');
            const userName = localStorage.getItem('userName') || "Você";
            
            if (!userId) {
                alert("Faça login para comentar.");
                return;
            }

            if (!localAtualId) {
                alert("Selecione um local primeiro.");
                return;
            }

            try {
                // INSERT direto no banco via SQL - sem JSON
                const formData = new URLSearchParams();
                formData.append('pessoaId', userId);
                formData.append('descricao', texto);
                formData.append('nota', '5');
                if (tagIdsSelecionadas.length > 0) {
                    formData.append('tagIds', tagIdsSelecionadas.join(','));
                }

                const response = await fetch(`http://localhost:8080/api/comentarios/direto/${localAtualId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: formData.toString()
                });

                if (response.ok) {
                    const comentarioSalvo = await response.json();
                    
                    // Recarrega os comentários do local para atualizar a UI
                    const localResponse = await fetch(`http://localhost:8080/api/locais/${localAtualId}`);
                    if (localResponse.ok) {
                        const localAtualizado = await localResponse.json();
                        await abrirPainel(localAtualizado);
                    }

                    textoComentarioInput.value = "";
                    tagIdsSelecionadas = [];
                    tagsChips.forEach(c => c.classList.remove('selected'));
                    
                    alert("Comentário adicionado com sucesso!");
                } else {
                    alert("Erro ao adicionar comentário.");
                }
            } catch (error) {
                console.error("Erro ao enviar comentário:", error);
                alert("Erro ao conectar com o servidor.");
            }
        });
    }

    function adicionarComentarioNaLista(autor, texto, tags = [], comentarioId = null, pessoaId = null, insertAtTop = true, avatar = null) {
        const panelComentarios = document.getElementById('panel-comentarios');
        const emptyMsg = panelComentarios.querySelector('.empty-state');
        if (emptyMsg) emptyMsg.remove();

        const div = document.createElement('div');
        div.classList.add('comentario-item');

        let tagsHtml = '';
        if (tags && tags.length > 0) {
            tagsHtml = '<div class="comment-tags">';
            tags.forEach(tag => tagsHtml += `<span class="mini-tag">${tag}</span>`);
            tagsHtml += '</div>';
        }
        // Mostrar avatar do comentário; fallback para avatar do usuário ou ícone padrão
        const storedAvatar = localStorage.getItem('userAvatar');
        const currentUserId = localStorage.getItem('userId');
        let avatarHtml = `<i class="fa-solid fa-circle-user default-avatar-icon"></i>`;
        // Função fallback para gerar avatar Dicebear
        const fallbackAvatar = (name) => `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(name || 'user')}`;
        if (avatar) {
            const fallback = fallbackAvatar(autor);
            avatarHtml = `<img src="${avatar}" alt="${autor}" class="comment-avatar-img" onerror="this.onerror=null;this.src='${fallback}';">`;
        } else if (storedAvatar && currentUserId && pessoaId && currentUserId == pessoaId) {
            const fallback = fallbackAvatar(autor);
            avatarHtml = `<img src="${storedAvatar}" alt="${autor}" class="comment-avatar-img" onerror="this.onerror=null;this.src='${fallback}';">`;
        }
        // Adiciona botões de ação (editar/excluir) somente se for o autor
        let actionsHtml = '';
        try {
            if (currentUserId && pessoaId && currentUserId == pessoaId && comentarioId !== null) {
                actionsHtml = `
                    <div class="comment-actions">
                        <button class="action-btn edit" title="Editar" onclick="editarComentarioMapa(${comentarioId})">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="action-btn delete" title="Excluir" onclick="excluirComentarioMapa(${comentarioId})">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                `;
            }
        } catch (e) {
            actionsHtml = '';
        }
        div.innerHTML = `
            <div class="comment-row">
                <div class="comment-avatar">${avatarHtml}</div>
                <div class="comment-body">
                    <div class="comment-author-row">
                        <strong>${autor}</strong>
                        ${actionsHtml}
                    </div>
                    <p class="comment-text">${texto}</p>
                    ${tagsHtml}
                </div>
            </div>
            <hr class="comment-separator">
        `;
        if (insertAtTop) {
            panelComentarios.insertBefore(div, panelComentarios.firstChild);
        } else {
            panelComentarios.appendChild(div);
        }
    }

    // Avalia se o selo de acessibilidade deve ser exibido para um local
    function avaliarSelo(local) {
        const sealEl = document.querySelector('.acessibility-seal');
        if (!sealEl) return;

        const comentarios = (local && local.comentarios) ? local.comentarios : [];
        // Se não atingir o número mínimo de comentários, não mostra o selo
        if (comentarios.length < MIN_COMMENTS_FOR_SEAL) {
            sealEl.style.display = 'none';
            // atualiza marcador para refletir ausência de selo
            atualizarMarcadorSelo(local);
            return;
        }

        const comTags = comentarios.filter(c => c.tags && c.tags.length > 0).length;
        const ratio = comTags / comentarios.length;

        if (ratio >= SEAL_THRESHOLD) {
            sealEl.style.display = 'flex';
        } else {
            sealEl.style.display = 'none';
        }
        // Atualiza também o marcador no mapa para refletir o selo
        atualizarMarcadorSelo(local);
    }

    function atualizarMarcadorSelo(local) {
        if (!local || !todosMarcadores) return;
        const entry = todosMarcadores.find(item => item.local && item.local.id == local.id);
        if (!entry) return;
        const has = temSelo(local);
        const iconColor = has ? 'green' : 'grey';
        const newIcon = criarIcone(iconColor);
        try { entry.marker.setIcon(newIcon); } catch (e) {}
        // Atualiza o objeto local armazenado
        entry.local = local;
    }

    // Retorna true se o local atende ao threshold para selo
    function temSelo(local) {
        const comentarios = (local && local.comentarios) ? local.comentarios : [];
        if (comentarios.length < MIN_COMMENTS_FOR_SEAL) return false;
        const comTags = comentarios.filter(c => c.tags && c.tags.length > 0).length;
        const ratio = comTags / comentarios.length;
        return ratio >= SEAL_THRESHOLD;
    }

    // Calcula a nota média (0..5) baseada nas tags dos comentários.
    // Para cada comentário: score = (numTags / TOTAL_TAG_OPTIONS) * 5
    function calcularNotaAcessibilidade(local) {
        // Nova lógica: nota baseada na proporção de comentários que possuem ao menos
        // uma tag. Isso alinha a nota com o critério do selo (percentual de comentários com tags).
        const comentarios = (local && local.comentarios) ? local.comentarios : [];
        if (comentarios.length === 0) return 0;
        const comTags = comentarios.filter(c => c.tags && c.tags.length > 0).length;
        const ratio = comTags / comentarios.length;
        const media = ratio * 5;
        return Math.round(media * 10) / 10; // uma casa decimal
    }

    // Gera HTML de estrelas (FontAwesome) com base em uma nota 0..5
    function renderStarsHtml(nota) {
        const full = Math.floor(nota);
        const rest = nota - full;
        let half = false;
        if (rest >= 0.75) {
            // arredonda para a estrela cheia
            half = false;
            nota = full + 1;
        } else if (rest >= 0.25) {
            half = true;
        }

        let html = '';
        for (let i = 0; i < full; i++) html += '<i class="fa-solid fa-star" style="color:var(--dourado)"></i>';
        if (half) html += '<i class="fa-solid fa-star-half-stroke" style="color:var(--dourado)"></i>';
        const totalIcons = full + (half ? 1 : 0);
        for (let i = totalIcons; i < 5; i++) html += '<i class="fa-regular fa-star" style="color:var(--cinza-claro)"></i>';
        html += `<span style="margin-left:8px;color:var(--texto-claro);font-weight:bold">${nota.toFixed(1)}</span>`;
        return html;
    }

    // Funções globais para editar/excluir comentário via backend
    window.editarComentarioMapa = async function(comentarioId) {
        const novoTexto = prompt('Edite seu comentário:');
        if (novoTexto !== null && novoTexto.trim() !== '') {
            try {
                const response = await fetch(`http://localhost:8080/api/comentarios/${comentarioId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ descricao: novoTexto, nota: 5 })
                });
                
                if (response.ok && localAtualId) {
                    const localResponse = await fetch(`http://localhost:8080/api/locais/${localAtualId}`);
                    if (localResponse.ok) {
                        const localAtualizado = await localResponse.json();
                        await abrirPainel(localAtualizado);
                    }
                    alert('Comentário atualizado!');
                } else {
                    alert('Erro ao atualizar comentário.');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor.');
            }
        }
    };

    window.excluirComentarioMapa = async function(comentarioId) {
        if (!confirm('Deseja apagar este comentário?')) return;
        
        try {
            const response = await fetch(`http://localhost:8080/api/comentarios/${comentarioId}`, {
                method: 'DELETE'
            });
            
            if (response.ok && localAtualId) {
                const localResponse = await fetch(`http://localhost:8080/api/locais/${localAtualId}`);
                if (localResponse.ok) {
                    const localAtualizado = await localResponse.json();
                    await abrirPainel(localAtualizado);
                }
                alert('Comentário excluído!');
            } else {
                alert('Erro ao excluir comentário.');
            }
        } catch (error) {
            console.error('Erro:', error);
            alert('Erro ao conectar com o servidor.');
        }
    };
    // --- Lógica para abrir o modal se vier do Perfil ---
    const params = new URLSearchParams(window.location.search);
    if (params.get('addLocal') === 'true') {
        // Encontra o modal que já existe no seu código
        const modal = document.getElementById('modal-novo-local');
        if (modal) {
            modal.style.display = 'flex'; // Abre o modal
        }
    }

});