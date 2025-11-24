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

    if (!localStorage.getItem('locaisSimulados')) {
        const locaisIniciais = [
            { 
                id: 1, 
                nome: "Shopping Vitória", 
                latitude: -20.3155, 
                longitude: -40.3128, 
                descricao: "Shopping acessível com rampas e elevadores.", 
                endereco: "Av. Américo Buaiz, 200", 
                proprietarioId: 1,
                comentarios: [
                    { autor: "Maria", texto: "Muito bom!", tags: ["Rampa"] }
                ]
            }
        ];
        localStorage.setItem('locaisSimulados', JSON.stringify(locaisIniciais));
    }

    function carregarPontos() {
        const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
        
        todosMarcadores.forEach(item => mapa.removeLayer(item.marker));
        todosMarcadores = [];

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
    }

    carregarPontos();

    const painel = document.getElementById('panel-detalhes');
    const botaoFecharPainel = document.getElementById('panel-close');

    function abrirPainel(local) {
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
            placeholderEl.style.display = 'none';
        } else {
            imgEl.style.display = 'none';
            placeholderEl.style.display = 'flex';
        }

        const panelComentarios = document.getElementById('panel-comentarios');
        panelComentarios.innerHTML = '';

        // Garantir que cada comentário tenha um avatar (gera um Dicebear quando ausente)
        if (local.comentarios && local.comentarios.length > 0) {
            const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
            const idxLocal = locais.findIndex(l => l.id == local.id);

            local.comentarios.forEach((c, idx) => {
                if (!c.avatar) {
                    try {
                        const seed = encodeURIComponent(c.autor || ('user' + idx));
                        c.avatar = `https://api.dicebear.com/7.x/adventurer/svg?seed=${seed}`;
                    } catch (e) {
                        c.avatar = null;
                    }
                }
                // Atualiza no storage se possível
                if (idxLocal !== -1) locais[idxLocal].comentarios[idx] = c;
            });
            if (idxLocal !== -1) {
                try { localStorage.setItem('locaisSimulados', JSON.stringify(locais)); } catch(e) {}
            }

            // Avaliar selo com base nos comentários
            avaliarSelo(local);

            // Calcula e mostra a média de avaliação baseada nas tags dos comentários
            const panelNotaEl = document.getElementById('panel-nota');
            if (panelNotaEl) {
                const avg = calcularNotaAcessibilidade(local);
                panelNotaEl.innerHTML = renderStarsHtml(avg);
                panelNotaEl.style.display = 'block';
            }

            // Renderiza os comentários (já com avatar presente)
            local.comentarios.forEach((c, idx) => {
                adicionarComentarioNaLista(c.autor, c.texto, c.tags, local.id, idx, false, c.avatar || null);
            });
        } else {
            avaliarSelo(local);
            const panelNotaEl = document.getElementById('panel-nota');
            if (panelNotaEl) panelNotaEl.style.display = 'none';
            panelComentarios.innerHTML = '<p class="empty-state">Nenhum comentário ainda.</p>';
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
            window.location.href = 'perfil.html';
        });
    }

    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    // Se o perfil pediu para abrir o modal de novo local, abre e remove a flag
    if (localStorage.getItem('openNovoLocal')) {
        if (modal) modal.style.display = 'flex';
        try { localStorage.removeItem('openNovoLocal'); } catch(e) {}
    }

    // Se o perfil solicitou zoom em um local (via 'Ver no mapa'), usamos 'zoomLocalId'
    if (localStorage.getItem('zoomLocalId')) {
        const zoomId = localStorage.getItem('zoomLocalId');
        try { localStorage.removeItem('zoomLocalId'); } catch(e) {}
        const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
        const alvo = locais.find(l => l.id == zoomId);
        if (alvo) {
            carregarPontos();
            abrirPainel(alvo);
            if (alvo.latitude && alvo.longitude) mapa.setView([alvo.latitude, alvo.longitude], 16);
        }
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
                const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&limit=1`);
                const data = await response.json();

                let lat, lon;

                if (data && data.length > 0) {
                    lat = parseFloat(data[0].lat);
                    lon = parseFloat(data[0].lon);
                } else {
                    const center = mapa.getCenter();
                    lat = center.lat;
                    lon = center.lng;
                    alert("Endereço não encontrado exato. Usando centro da tela.");
                }

                const novoLocal = {
                    id: Date.now(),
                    nome: nome,
                    endereco: endereco,
                    descricao: descricao,
                    latitude: lat,
                    longitude: lon,
                    proprietarioId: userId,
                    comentarios: []
                };

                const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
                locais.push(novoLocal);
                localStorage.setItem('locaisSimulados', JSON.stringify(locais));

                alert("Local salvo com sucesso!");
                modal.style.display = 'none';
                formNovoLocal.reset();
                
                carregarPontos();
                mapa.setView([lat, lon], 16);

            } catch (error) {
                console.error(error);
                alert("Erro ao buscar endereço.");
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
    function updateSuggestions(term) {
        if (!suggestionsEl) return;
        const raw = (term || '').trim();
        if (!raw) { suggestionsEl.innerHTML = ''; suggestionsEl.style.display = 'none'; return; }

        const termo = normalizeForSearch(raw);
        const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
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
            const tag = this.getAttribute('data-tag'); 
            
            if (this.classList.contains('selected')) {
                tagIdsSelecionadas.push(tag);
            } else {
                tagIdsSelecionadas = tagIdsSelecionadas.filter(t => t !== tag);
            }
        });
    });

    const btnEnviarComentario = document.getElementById('btn-enviar-avaliacao');
    const textoComentarioInput = document.getElementById('novo-comentario-texto');

    if (btnEnviarComentario) {
        btnEnviarComentario.addEventListener('click', () => {
            const texto = textoComentarioInput.value;
            if (texto.trim() === "") { alert("Escreva algo!"); return; }
            
            const userName = localStorage.getItem('userName') || "Você";
            
                const novoComentario = {
                    autor: userName,
                    texto: texto,
                    tags: [...tagIdsSelecionadas],
                    avatar: localStorage.getItem('userAvatar') || null
                };

            const locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
            const localIndex = locais.findIndex(l => l.id == localAtualId);
            
            if (localIndex !== -1) {
                if (!locais[localIndex].comentarios) locais[localIndex].comentarios = [];
                locais[localIndex].comentarios.unshift(novoComentario);
                localStorage.setItem('locaisSimulados', JSON.stringify(locais));

                // Inserir novo comentário no topo
                const userAvatar = localStorage.getItem('userAvatar') || null;
                adicionarComentarioNaLista(userName, texto, tagIdsSelecionadas, locais[localIndex].id, 0, true, userAvatar);
                // Reavaliar selo após novo comentário
                avaliarSelo(locais[localIndex]);
                textoComentarioInput.value = "";
                tagIdsSelecionadas = [];
                tagsChips.forEach(c => c.classList.remove('selected'));
            }
        });
    }

    function adicionarComentarioNaLista(autor, texto, tags = [], localId = null, index = null, insertAtTop = true, avatar = null) {
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
        const currentUser = localStorage.getItem('userName');
        let avatarHtml = `<i class="fa-solid fa-circle-user default-avatar-icon"></i>`;
        // Função fallback para gerar avatar Dicebear
        const fallbackAvatar = (name) => `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(name || 'user')}`;
        if (avatar) {
            const fallback = fallbackAvatar(autor);
            avatarHtml = `<img src="${avatar}" alt="${autor}" class="comment-avatar-img" onerror="this.onerror=null;this.src='${fallback}';">`;
        } else if (storedAvatar && currentUser && autor === currentUser) {
            const fallback = fallbackAvatar(autor);
            avatarHtml = `<img src="${storedAvatar}" alt="${autor}" class="comment-avatar-img" onerror="this.onerror=null;this.src='${fallback}';">`;
        }
        // Adiciona botões de ação (editar/excluir) somente se for o autor
        let actionsHtml = '';
        try {
            const current = localStorage.getItem('userName');
            if (current && autor === current && localId !== null && index !== null) {
                actionsHtml = `
                    <div class="comment-actions">
                        <button class="action-btn edit" title="Editar" onclick="editarComentarioMapa(${localId}, ${index})">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="action-btn delete" title="Excluir" onclick="excluirComentarioMapa(${localId}, ${index})">
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

    // Funções globais para editar/excluir comentário no mapa (sem recarregar a página)
    window.editarComentarioMapa = function(idLocal, indexComentario) {
        let locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
        const localIdx = locais.findIndex(l => l.id == idLocal);
        if (localIdx === -1) return;

        const textoAtual = (locais[localIdx].comentarios && locais[localIdx].comentarios[indexComentario]) ? locais[localIdx].comentarios[indexComentario].texto : '';
        const novoTexto = prompt('Edite seu comentário:', textoAtual);
        if (novoTexto !== null && novoTexto.trim() !== '') {
            locais[localIdx].comentarios[indexComentario].texto = novoTexto;
            localStorage.setItem('locaisSimulados', JSON.stringify(locais));
            // Reabrir painel para atualizar a lista
            const localData = locais[localIdx];
            abrirPainel(localData);
        }
    };

    window.excluirComentarioMapa = function(idLocal, indexComentario) {
        if (!confirm('Deseja apagar este comentário?')) return;
        let locais = JSON.parse(localStorage.getItem('locaisSimulados')) || [];
        const localIdx = locais.findIndex(l => l.id == idLocal);
        if (localIdx === -1) return;
        if (!locais[localIdx].comentarios) return;
        locais[localIdx].comentarios.splice(indexComentario, 1);
        localStorage.setItem('locaisSimulados', JSON.stringify(locais));
        // Reabrir painel para atualizar a lista
        const localData = locais[localIdx];
        abrirPainel(localData);
    };
});