document.addEventListener('DOMContentLoaded', () => {
    const mapa = L.map('mapa', { zoomControl: false }).setView([-20.3155, -40.3128], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; OSM' }).addTo(mapa);
    L.control.zoom({ position: 'topright' }).addTo(mapa);

    let todosMarcadores = [];
    let localAtualId = null;
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');
    const userAvatar = localStorage.getItem('userAvatar');

    const painel = document.getElementById('panel-detalhes');
    const btnAddLocal = document.getElementById('btn-zoom-out');
    const tagsChipsWrapper = document.querySelector('.tags-wrapper');

    if (userRole !== 'proprietario' && btnAddLocal) {
        btnAddLocal.style.display = 'none';
    }

    carregarLocais();
    gerarTags();
    configurarBusca();
    verificarRedirecionamento();

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

    async function carregarLocais() {
        try {
            const locais = await fetchAPI('/locais');

            todosMarcadores.forEach(m => mapa.removeLayer(m.marker));
            todosMarcadores = [];

            locais.forEach(local => {
                if (local.latitude && local.longitude) {
                    let iconColor = 'grey';
                    if (local.possuiSelo) {
                        iconColor = 'green';
                    } else if (local.mediaAvaliacao !== null && local.mediaAvaliacao > 0 && local.mediaAvaliacao < 2.5) {
                        iconColor = 'red';
                    }
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
            console.error(error);
        }
    }

    async function abrirPainel(local) {
        localAtualId = local.id;
        document.getElementById('panel-titulo').textContent = local.nome;
        document.getElementById('panel-descricao').textContent = local.descricao || "";

        // --- ALTERAÇÃO AQUI: Renderiza a estrela amarela + nota ---
        const panelNotaEl = document.getElementById('panel-nota');
        panelNotaEl.innerHTML = renderNota(local.mediaAvaliacao || 0);

        const sealEl = document.querySelector('.acessibility-seal');
        if (local.possuiSelo) {
            sealEl.style.display = 'flex';
            sealEl.querySelector('span').textContent = "Selo de Acessibilidade";
            sealEl.querySelector('i').className = "fa-solid fa-circle-check";
            sealEl.style.color = "#27ae60";
        } else {
            sealEl.style.display = 'none';
        }

        document.getElementById('panel-imagem-principal').style.display = 'none';
        document.getElementById('panel-imagem-placeholder').style.display = 'flex';

        const ratingSection = document.querySelector('.user-rating-section');
        if (userRole === 'proprietario') {
            if (ratingSection) ratingSection.style.display = 'none';
        } else {
            if (ratingSection) ratingSection.style.display = 'block';
            const myAvatarIcon = document.querySelector('.user-avatar-icon');
            if (myAvatarIcon && userAvatar) {
                myAvatarIcon.innerHTML = `<img src="${userAvatar}" style="width:100%; height:100%; border-radius:50%; object-fit:cover;">`;
            }
        }

        carregarComentarios(local.id);
        painel.classList.add('is-open');
        document.body.style.setProperty('--panel-width', '400px');
    }

    async function carregarComentarios(idLocal) {
        const container = document.getElementById('panel-comentarios');
        container.innerHTML = '<p>Carregando...</p>';

        try {
            const comentarios = await fetchAPI(`/comentarios/local/${idLocal}`);
            container.innerHTML = '';

            if (!comentarios || comentarios.length === 0) {
                container.innerHTML = '<p class="empty-state">Seja o primeiro a avaliar!</p>';
                return;
            }

            comentarios.forEach(c => {
                const nomeParaAvatar = c.nomeUsuario || "anonimo";
                const avatarImgUrl = `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(nomeParaAvatar)}`;
                const avatarImgTag = `<img src="${avatarImgUrl}" class="comment-avatar-img">`;

                let tagsHtml = '';
                if (c.tags && c.tags.length > 0) {
                    tagsHtml = '<div class="comment-tags">';
                    c.tags.forEach(tagKey => {
                        const info = ENUM_TAGS[tagKey] || { label: tagKey };
                        tagsHtml += `<span class="mini-tag">${info.label}</span>`;
                    });
                    tagsHtml += '</div>';
                }

                let btnDelete = '';
                if (userId && c.idUsuario == userId) {
                    btnDelete = `<button class="action-btn delete" onclick="deletarComentario(${c.id})"><i class="fa-solid fa-trash"></i></button>`;
                }

                const div = document.createElement('div');
                div.className = 'comentario-item';
                div.innerHTML = `
                    <div class="comment-row">
                        <div class="comment-avatar">${avatarImgTag}</div>
                        <div class="comment-body">
                            <div class="comment-author-row">
                                <strong>${c.nomeUsuario || "Usuário"} ${userId && c.idUsuario == userId ? '(Você)' : ''}</strong>
                                ${btnDelete}
                            </div>
                            <p class="comment-text">${c.texto}</p>
                            ${tagsHtml}
                        </div>
                    </div>
                    <hr class="comment-separator">
                `;
                container.appendChild(div);
            });
        } catch (e) {
            console.error(e);
            container.innerHTML = '<p>Erro ao carregar avaliações.</p>';
        }
    }

    function gerarTags() {
        if (!tagsChipsWrapper) return;
        tagsChipsWrapper.innerHTML = '';

        Object.keys(ENUM_TAGS).forEach(key => {
            const tagInfo = ENUM_TAGS[key];
            const span = document.createElement('span');
            span.className = 'tag-chip';
            span.textContent = tagInfo.label;
            span.dataset.key = key;

            if (tagInfo.negativa) {
                span.style.border = "1px solid #e74c3c";
                span.style.color = "#c0392b";
            }

            span.addEventListener('click', () => {
                if (tagInfo.negativa) {
                    if (span.classList.contains('selected')) {
                        span.classList.remove('selected');
                    } else {
                        document.querySelectorAll('.tag-chip').forEach(el => el.classList.remove('selected'));
                        span.classList.add('selected');
                    }
                } else {
                    const tagInadequado = document.querySelector(`.tag-chip[data-key="INADEQUADO"]`);
                    if (tagInadequado) tagInadequado.classList.remove('selected');
                    span.classList.toggle('selected');
                }
            });

            tagsChipsWrapper.appendChild(span);
        });
    }

    const btnEnviar = document.getElementById('btn-enviar-avaliacao');
    if (btnEnviar) {
        btnEnviar.addEventListener('click', async () => {
            if (!userId) { alert("Faça login para avaliar."); return; }

            const textoDigitado = document.getElementById('novo-comentario-texto').value;
            const tagsSelecionadas = Array.from(document.querySelectorAll('.tag-chip.selected')).map(el => el.dataset.key);

            if (tagsSelecionadas.length === 0) {
                alert("Selecione ao menos uma tag.");
                return;
            }

            const payload = {
                texto: textoDigitado,
                idUsuario: parseInt(userId),
                idLocal: localAtualId,
                tags: tagsSelecionadas
            };

            try {
                await fetchAPI('/comentarios', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                document.getElementById('novo-comentario-texto').value = '';
                document.querySelectorAll('.tag-chip').forEach(el => el.classList.remove('selected'));

                await carregarLocais();
                const localRecarregado = todosMarcadores.find(m => m.local.id === localAtualId).local;
                abrirPainel(localRecarregado);
            } catch (error) {
                alert("Erro ao enviar: " + error.message);
            }
        });
    }

    window.deletarComentario = async function (id) {
        if (confirm("Apagar comentário?")) {
            try {
                await fetchAPI(`/comentarios/${id}`, { method: 'DELETE' });
                await carregarLocais();
                const localRecarregado = todosMarcadores.find(m => m.local.id === localAtualId).local;
                abrirPainel(localRecarregado);
            } catch (e) {
                alert("Erro ao excluir.");
            }
        }
    };

    const formNovoLocal = document.getElementById('form-novo-local');
    if (formNovoLocal) {
        formNovoLocal.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!userId) return;

            const nome = document.getElementById('local-nome').value;
            const endereco = document.getElementById('local-endereco').value;
            const descricao = document.getElementById('local-descricao').value;
            const btnSalvar = formNovoLocal.querySelector('.btn-salvar');

            btnSalvar.textContent = "Salvando...";
            btnSalvar.disabled = true;

            const payload = {
                nome: nome,
                endereco: endereco,
                descricao: descricao,
                idProprietario: parseInt(userId)
            };

            try {
                await fetchAPI('/locais', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                document.getElementById('modal-novo-local').style.display = 'none';
                formNovoLocal.reset();
                carregarLocais();
            } catch (error) {
                alert("Erro ao cadastrar.");
            } finally {
                btnSalvar.textContent = "Salvar Local";
                btnSalvar.disabled = false;
            }
        });
    }

    function renderNota(nota) {
        if (nota === null || nota === undefined) nota = 0;
        return `<i class="fa-solid fa-star" style="color: gold; font-size: 1.2em;"></i> <span style="font-size: 1em;">${nota.toFixed(1)}/5</span>`;
    }

    document.getElementById('panel-close').addEventListener('click', () => {
        painel.classList.remove('is-open');
        document.body.style.setProperty('--panel-width', '0px');
    });

    document.getElementById('btn-zoom-in')?.addEventListener('click', () => window.location.href = 'perfil.html');
    document.getElementById('close-modal')?.addEventListener('click', () => document.getElementById('modal-novo-local').style.display = 'none');
    if (btnAddLocal) btnAddLocal.addEventListener('click', () => document.getElementById('modal-novo-local').style.display = 'flex');

    function configurarBusca() {
        const searchInput = document.querySelector('.search-input');
        const searchContainer = document.querySelector('.search-bar-container');

        let suggestionsEl = document.createElement('div');
        suggestionsEl.className = 'search-suggestions';
        suggestionsEl.style.display = 'none';
        searchContainer.appendChild(suggestionsEl);

        searchInput.addEventListener('input', (e) => {
            const term = e.target.value.toLowerCase();
            suggestionsEl.innerHTML = '';
            if (!term) { suggestionsEl.style.display = 'none'; return; }

            const matches = todosMarcadores.filter(m =>
                m.local.nome.toLowerCase().includes(term) ||
                m.local.endereco.toLowerCase().includes(term)
            ).slice(0, 5);

            if (matches.length === 0) { suggestionsEl.style.display = 'none'; return; }

            matches.forEach(m => {
                const div = document.createElement('div');
                div.className = 'search-suggestion';
                div.innerHTML = `<strong>${m.local.nome}</strong><br><small>${m.local.endereco}</small>`;
                div.addEventListener('click', () => {
                    abrirPainel(m.local);
                    mapa.setView([m.local.latitude, m.local.longitude], 16);
                    suggestionsEl.style.display = 'none';
                    searchInput.value = '';
                });
                suggestionsEl.appendChild(div);
            });
            suggestionsEl.style.display = 'block';
        });

        document.addEventListener('click', (e) => {
            if (!searchContainer.contains(e.target)) suggestionsEl.style.display = 'none';
        });
    }

    function verificarRedirecionamento() {
        const zoomId = localStorage.getItem('zoomLocalId');
        if (zoomId) {
            localStorage.removeItem('zoomLocalId');
            setTimeout(() => {
                const alvo = todosMarcadores.find(m => m.local.id == zoomId);
                if (alvo) {
                    mapa.setView([alvo.local.latitude, alvo.local.longitude], 16);
                    abrirPainel(alvo.local);
                }
            }, 500);
        }

        if (localStorage.getItem('openNovoLocal')) {
            localStorage.removeItem('openNovoLocal');
            document.getElementById('modal-novo-local').style.display = 'flex';
        }
    }
});