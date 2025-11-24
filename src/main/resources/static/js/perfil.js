document.addEventListener('DOMContentLoaded', () => {

    // 1. DADOS DO USUÁRIO
    const userId = localStorage.getItem('userId');
    const userName = localStorage.getItem('userName') || "Usuário";
    const userEmail = localStorage.getItem('userEmail') || "email@teste.com";
    const userRole = localStorage.getItem('userRole') || "usuario";

    // Redireciona se não estiver logado
    if (!localStorage.getItem('userRole')) {
        window.location.href = '../html/login.html';
    }

    // Preencher Campos
    const inputName = document.getElementById('user-name-input');
    const inputEmail = document.getElementById('user-email-input');
    
    if(inputName) inputName.value = userName;
    if(inputEmail) inputEmail.value = userEmail;
    
    const roleLabel = document.getElementById('user-role-label');
    if(roleLabel) roleLabel.textContent = userRole === 'proprietario' ? 'Proprietário' : 'Usuário Comum';

    // Avatar
    const avatarSalvo = localStorage.getItem('userAvatar');
    if (avatarSalvo) {
        document.querySelector('.img-container').innerHTML = `<img src="${avatarSalvo}">`;
    }

    // 2. CONTROLE DE COLUNAS (Quem vê o quê)
    const colComentarios = document.getElementById('col-comentarios');
    const colMeusLocais = document.getElementById('col-meus-locais');
    const colFavoritos = document.getElementById('col-favoritos');

    if(colFavoritos) colFavoritos.style.display = 'none';

    if (userRole === 'proprietario') {
        // PROPRIETÁRIO: Vê Locais, NÃO VÊ Comentários
        if(colComentarios) colComentarios.style.display = 'none';
        if(colMeusLocais) {
            colMeusLocais.style.display = 'flex'; 
            colMeusLocais.style.flexDirection = 'column';
            carregarLocaisDoProprietario(userId);
        }
    } else {
        // USUÁRIO: Vê Comentários, NÃO VÊ Locais
        if(colMeusLocais) colMeusLocais.style.display = 'none';
        if(colComentarios) {
            colComentarios.style.display = 'flex';
            colComentarios.style.flexDirection = 'column';
            carregarMeusComentarios(userName);
        }
    }

    // 3. FUNÇÕES DE CARREGAMENTO

    async function carregarLocaisDoProprietario(id) {
        const lista = document.getElementById('lista-meus-locais');
        lista.innerHTML = '<p class="empty-state">Carregando...</p>';
        
        try {
            const response = await fetch(`http://localhost:8080/api/locais`);
            if (!response.ok) {
                lista.innerHTML = '<p class="empty-state">Erro ao carregar locais.</p>';
                return;
            }
            
            const locais = await response.json();
            // Filtra apenas os locais desse proprietário
            const meus = locais.filter(l => l.proprietario && l.proprietario.id == id);
            
            lista.innerHTML = '';

            if(meus.length === 0) {
                lista.innerHTML = '<p class="empty-state">Nenhum local cadastrado.</p>';
                return;
            }

        // Helper: calcula nota 0..5 baseada na proporção de comentários com tags
        function calcularNotaAcessibilidade(local) {
            const comentarios = (local && local.comentarios) ? local.comentarios : [];
            if (comentarios.length === 0) return 0;
            const comTags = comentarios.filter(c => c.tags && c.tags.length > 0).length;
            const ratio = comTags / comentarios.length;
            const media = ratio * 5;
            return Math.round(media * 10) / 10;
        }

        // Helper: renderiza estrelas FontAwesome e o valor numérico
        function renderStarsHtml(nota) {
            const full = Math.floor(nota);
            const rest = nota - full;
            let half = false;
            let displayNota = nota;
            if (rest >= 0.75) {
                displayNota = full + 1;
            } else if (rest >= 0.25) {
                half = true;
            }
            let html = '<div class="card-rating">';
            for (let i = 0; i < full; i++) html += '<i class="fa-solid fa-star" style="color:var(--dourado)"></i>';
            if (half) html += '<i class="fa-solid fa-star-half-stroke" style="color:var(--dourado)"></i>';
            const totalIcons = full + (half ? 1 : 0);
            for (let i = totalIcons; i < 5; i++) html += '<i class="fa-regular fa-star" style="color:var(--cinza-claro)"></i>';
            html += `<span style="margin-left:8px;color:var(--texto-claro);font-weight:bold">${displayNota.toFixed(1)}</span>`;
            html += '</div>';
            return html;
        }

        meus.forEach(local => {
            const div = document.createElement('div');
            div.className = 'card-item';
            
            const nota = calcularNotaAcessibilidade(local);
            const starsHtml = renderStarsHtml(nota);
            div.innerHTML = `
                <div class="card-header-row">
                    <div>
                        <h4>${local.nome}</h4>
                        ${starsHtml}
                    </div>
                    <div class="card-actions">
                        <button class="action-btn edit" onclick="editarLocalPerfil(${local.id})" title="Editar Nome">
                            <i class="fa-solid fa-pen"></i>
                        </button>
                        <button class="action-btn delete" onclick="excluirLocalPerfil(${local.id})" title="Excluir Local">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </div>
                </div>
                <p class="card-subtitle">${local.endereco || 'Sem endereço'}</p>

                <div class="see-on-map">
                    <small onclick="irParaMapa(${local.id})">
                        Ver no mapa <i class="fa-solid fa-arrow-right"></i>
                    </small>
                </div>
            `;
            lista.appendChild(div);
        });
        } catch (error) {
            console.error('Erro ao carregar locais:', error);
            lista.innerHTML = '<p class="empty-state">Erro ao carregar locais.</p>';
        }
    }

    async function carregarMeusComentarios(nomeUsuario) {
        const lista = document.getElementById('lista-comentarios');
        lista.innerHTML = '<p class="empty-state">Carregando...</p>';
        
        try {
            const response = await fetch(`http://localhost:8080/api/comentarios/usuario/${userId}`);
            if (!response.ok) {
                if (response.status === 204) {
                    lista.innerHTML = '<p class="empty-state">Você ainda não fez nenhum comentário.</p>';
                } else {
                    lista.innerHTML = '<p class="empty-state">Erro ao carregar comentários.</p>';
                }
                return;
            }
            
            const comentarios = await response.json();
            lista.innerHTML = '';

            if (!comentarios || comentarios.length === 0) {
                lista.innerHTML = '<p class="empty-state">Você ainda não fez nenhum comentário.</p>';
                return;
            }

            comentarios.forEach(coment => {
                const local = coment.local || {};
                const tagNames = coment.tags ? coment.tags.map(t => t.nome) : [];
                const div = document.createElement('div');
                div.className = 'card-item';
                
                // Renderizar tags do comentário (se houver)
                let tagsHtml = '';
                if (tagNames.length > 0) {
                    tagsHtml = '<div class="comment-tags">';
                    tagNames.forEach(tag => tagsHtml += `<span class="mini-tag">${tag}</span>`);
                    tagsHtml += '</div>';
                }

                div.innerHTML = `
                    <div class="card-header-row">
                        <h4>${local.nome || 'Local'}</h4>
                        <div class="card-actions">
                            <button class="action-btn edit" onclick="editarComentarioPerfil(${coment.id})">
                                <i class="fa-solid fa-pen"></i>
                            </button>
                            <button class="action-btn delete" onclick="excluirComentarioPerfil(${coment.id})">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    
                    <p class="comment-quote">"${coment.descricao || coment.texto || ''}"</p>
                    ${tagsHtml}
                    
                    <div class="see-on-map">
                        <small onclick="irParaMapa(${local.id})">
                            Ver no mapa <i class="fa-solid fa-arrow-right"></i>
                        </small>
                    </div>
                `;
                lista.appendChild(div);
            });
        } catch (error) {
            console.error('Erro ao carregar comentários:', error);
            lista.innerHTML = '<p class="empty-state">Erro ao carregar comentários.</p>';
        }
    }

    // 4. LÓGICA DOS BOTÕES (Global para o HTML acessar)

    // --- Ações de Local (Proprietário) ---
    window.editarLocalPerfil = async function(idLocal) {
        const novoNome = prompt("Novo nome para o local:");
        if (novoNome && novoNome.trim()) {
            try {
                const response = await fetch(`http://localhost:8080/api/locais/${idLocal}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ nome: novoNome })
                });
                
                if (response.ok) {
                    alert('Local atualizado!');
                    location.reload();
                } else {
                    alert('Erro ao atualizar local.');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor.');
            }
        }
    };

    window.excluirLocalPerfil = async function(idLocal) {
        if (confirm("Tem certeza que deseja excluir este local permanentemente?")) {
            try {
                const response = await fetch(`http://localhost:8080/api/locais/${idLocal}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    alert('Local excluído!');
                    location.reload();
                } else {
                    alert('Erro ao excluir local.');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor.');
            }
        }
    };

    // --- Ações de Comentário (Usuário) ---
    window.editarComentarioPerfil = async function(comentarioId) {
        const novoTexto = prompt("Edite seu comentário:");
        
        if (novoTexto !== null && novoTexto.trim() !== "") {
            try {
                const response = await fetch(`http://localhost:8080/api/comentarios/${comentarioId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ descricao: novoTexto, nota: 5 })
                });
                
                if (response.ok) {
                    alert('Comentário atualizado!');
                    location.reload();
                } else {
                    alert('Erro ao atualizar comentário.');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor.');
            }
        }
    };

    window.excluirComentarioPerfil = async function(comentarioId) {
        if (confirm("Deseja apagar este comentário?")) {
            try {
                const response = await fetch(`http://localhost:8080/api/comentarios/${comentarioId}`, {
                    method: 'DELETE'
                });
                
                if (response.ok) {
                    alert('Comentário excluído!');
                    location.reload();
                } else {
                    alert('Erro ao excluir comentário.');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro ao conectar com o servidor.');
            }
        }
    };

    // Redirecionamento Mapa
    window.irParaMapa = function(idLocal) {
        // Navega para o mapa - o mapa carregará todos os locais do backend
        window.location.href = '../html/mapa.html';
    };

    // 5. LOGOUT
    const btnLogout = document.getElementById('btn-logout');
    if(btnLogout) {
        btnLogout.addEventListener('click', (e) => {
            e.preventDefault(); // Evita comportamento padrão de link se houver
            localStorage.removeItem('userRole');
            localStorage.removeItem('userId');
            localStorage.removeItem('userName');
            localStorage.removeItem('userEmail');
            window.location.href = '../html/login.html';
        });
    }

    // 6. MODAL AVATAR (Mantido igual)
    const btnTrocar = document.getElementById('btn-trocar-imagem');
    const modalAvatar = document.getElementById('modal-avatar');
    // Botão no HTML usa id "btn-fechar-avatar" — antes estava com id incorreto
    const btnCancelar = document.getElementById('btn-fechar-avatar');
    // A grid de avatares no HTML usa a classe "avatar-grid"
    const grid = document.querySelector('.avatar-grid');

    const listaAvatares = [
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Felix",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Aneka",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Midnight",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Abby",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Bella",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Brian"
    ];

    function renderAvatares() {
        if(!grid) return;
        grid.innerHTML = '';
        listaAvatares.forEach(url => {
            const img = document.createElement('img');
            img.src = url;
            img.className = 'avatar-option';
            img.onclick = () => {
                document.querySelector('.img-container').innerHTML = `<img src="${url}">`;
                localStorage.setItem('userAvatar', url);
                if(modalAvatar) modalAvatar.classList.remove('is-open');
            };
            grid.appendChild(img);
        });
    }

    if (btnTrocar) {
        btnTrocar.addEventListener('click', (e) => { 
            e.preventDefault(); 
            if(modalAvatar) modalAvatar.classList.add('is-open'); 
            renderAvatares(); 
        });
    }
    
    if (btnCancelar) {
        btnCancelar.addEventListener('click', () => {
            if(modalAvatar) modalAvatar.classList.remove('is-open');
        });
    }

    // Ação usada ao clicar em "Adicionar Novo Local" no perfil (navega ao mapa)
    window.adicionarNovoLocalPerfil = function() {
        // Navega para o mapa onde o proprietário pode adicionar novo local
        window.location.href = 'mapa.html';
    };

    // Lógica de edição de inputs (Nome/Email)
    const setupEdit = (btnId, inputId, storageKey) => {
        const btn = document.getElementById(btnId);
        const input = document.getElementById(inputId);
        if(btn && input) {
            btn.addEventListener('click', () => {
                if (input.disabled) {
                    input.disabled = false; input.focus(); btn.textContent = "Salvar";
                    btn.classList.add('saving');
                } else {
                    input.disabled = true; btn.textContent = "Editar";
                    btn.classList.remove('saving');
                    if (storageKey) localStorage.setItem(storageKey, input.value);
                    alert("Atualizado!");
                }
            });
        }
    };
    
    // Se os botões existirem no HTML, adapte os IDs abaixo se necessário
    setupEdit('btn-edit-name', 'user-name-input', 'userName'); // Ela não tinha IDs nos botões de editar no HTML original, mas se tiver, funciona.
});