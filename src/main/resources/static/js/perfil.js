document.addEventListener('DOMContentLoaded', () => {
    const userId = localStorage.getItem('userId');
    const userRole = localStorage.getItem('userRole');

    if (!userId) {
        window.location.href = '../html/login.html';
        return;
    }

    carregarDados();
    configurarEdicao();

    async function carregarDados() {
        const endpointUser = userRole === 'proprietario' ? `/proprietarios/${userId}` : `/usuarios/${userId}`;
        try {
            const userData = await fetchAPI(endpointUser);
            
            const inputName = document.getElementById('user-name-input');
            const inputEmail = document.getElementById('user-email-input');
            if(inputName) inputName.value = userData.nome;
            if(inputEmail) inputEmail.value = userData.email;
            
            document.getElementById('user-role-label').innerText = userRole === 'proprietario' ? 'Proprietário' : 'Usuário';
            
            const avatar = localStorage.getItem('userAvatar') || `https://api.dicebear.com/7.x/adventurer/svg?seed=${userData.nome}`;
            document.querySelector('.img-container').innerHTML = `<img src="${avatar}">`;

            const colLocais = document.getElementById('col-meus-locais');
            const colComentarios = document.getElementById('col-comentarios');

            if (userRole === 'proprietario') {
                if(colComentarios) colComentarios.style.display = 'none';
                if(colLocais) {
                    colLocais.style.display = 'flex';
                    colLocais.style.flexDirection = 'column';
                    listarLocais();
                }
            } else {
                if(colLocais) colLocais.style.display = 'none';
                if(colComentarios) {
                    colComentarios.style.display = 'flex';
                    colComentarios.style.flexDirection = 'column';
                    listarComentarios();
                }
            }
        } catch (error) {
            console.error(error);
        }
    }

    async function listarLocais() {
        const container = document.getElementById('lista-meus-locais');
        container.innerHTML = 'Carregando...';

        try {
            const todos = await fetchAPI('/locais');
            const meus = todos.filter(l => l.idProprietario == userId);

            container.innerHTML = '';
            if (meus.length === 0) {
                container.innerHTML = '<p class="empty-state">Nenhum local cadastrado.</p>';
                return;
            }

            meus.forEach(local => {
                const div = document.createElement('div');
                div.className = 'card-item click-map';
                div.style.cursor = 'pointer';

                div.addEventListener('click', () => {
                    localStorage.setItem('zoomLocalId', local.id);
                    window.location.href = '../html/mapa.html';
                });

                div.innerHTML = `
                    <div class="card-header-row">
                        <h4>${local.nome}</h4>
                        <div class="card-actions">
                            <button class="action-btn edit" id="btn-edit-${local.id}"><i class="fa-solid fa-pen"></i></button>
                            <button class="action-btn delete" id="btn-del-${local.id}"><i class="fa-solid fa-trash"></i></button>
                        </div>
                    </div>
                    <p>${local.endereco}</p>
                    <small style="color:blue">Ver no mapa <i class="fa-solid fa-arrow-right"></i></small>
                `;
                container.appendChild(div);

                document.getElementById(`btn-edit-${local.id}`).addEventListener('click', (e) => {
                    e.stopPropagation(); 
                    editarLocal(local.id, local.nome);
                });
                
                document.getElementById(`btn-del-${local.id}`).addEventListener('click', (e) => {
                    e.stopPropagation(); 
                    excluirLocal(local.id);
                });
            });
        } catch (error) {
            container.innerHTML = 'Erro ao carregar locais.';
        }
    }

    async function listarComentarios() {
        const container = document.getElementById('lista-comentarios');
        container.innerHTML = 'Carregando...';

        try {
            const locais = await fetchAPI('/locais');
            let meusComentarios = [];

            for (const local of locais) {
                const comments = await fetchAPI(`/comentarios/local/${local.id}`);
                if (comments && comments.length > 0) {
                    const meus = comments.filter(c => c.idUsuario == userId);
                    meus.forEach(c => {
                        c.nomeLocal = local.nome;
                        c.idLocal = local.id; 
                        meusComentarios.push(c);
                    });
                }
            }

            container.innerHTML = '';
            if (meusComentarios.length === 0) {
                container.innerHTML = '<p class="empty-state">Nenhum comentário.</p>';
                return;
            }

            meusComentarios.forEach(c => {
                const div = document.createElement('div');
                div.className = 'card-item click-map';
                div.style.cursor = 'pointer';

                div.addEventListener('click', () => {
                    localStorage.setItem('zoomLocalId', c.idLocal);
                    window.location.href = '../html/mapa.html';
                });

                div.innerHTML = `
                    <div class="card-header-row">
                        <h4>${c.nomeLocal}</h4>
                        <div class="card-actions">
                            <button class="action-btn edit" id="btn-com-edit-${c.id}"><i class="fa-solid fa-pen"></i></button>
                            <button class="action-btn delete" id="btn-com-del-${c.id}"><i class="fa-solid fa-trash"></i></button>
                        </div>
                    </div>
                    <p style="font-style:italic">"${c.comentario}"</p>
                    <div style="margin-top:5px">
                        ${c.tags.map(t => `<span class="mini-tag">${t}</span>`).join(' ')}
                    </div>
                    <small style="color:blue; display:block; margin-top:5px">Ver no mapa <i class="fa-solid fa-arrow-right"></i></small>
                `;
                container.appendChild(div);

                document.getElementById(`btn-com-edit-${c.id}`).addEventListener('click', (e) => {
                    e.stopPropagation();
                    editarComentario(c);
                });

                document.getElementById(`btn-com-del-${c.id}`).addEventListener('click', (e) => {
                    e.stopPropagation();
                    excluirComentario(c.id);
                });
            });
        } catch (error) {
            container.innerHTML = 'Erro ao carregar comentários.';
        }
    }

    async function editarLocal(id, nomeAtual) {
        const novoNome = prompt("Novo nome:", nomeAtual);
        if (novoNome && novoNome !== nomeAtual) {
            try {
                const locais = await fetchAPI('/locais');
                const localObj = locais.find(l => l.id === id);
                if(localObj) {
                    localObj.nome = novoNome;
                    await fetchAPI(`/locais/${id}`, { 
                        method: 'PUT', 
                        body: JSON.stringify(localObj) 
                    });
                    listarLocais();
                }
            } catch (e) { alert("Erro ao editar."); }
        }
    }

    async function excluirLocal(id) {
        if(confirm("Excluir local?")) {
            try {
                await fetchAPI(`/locais/${id}`, { method: 'DELETE' });
                listarLocais();
            } catch (e) { alert("Erro ao excluir."); }
        }
    }

    async function editarComentario(comentarioObj) {
        const novoTexto = prompt("Editar comentário:", comentarioObj.comentario);
        if (novoTexto !== null && novoTexto !== comentarioObj.comentario) {
            try {
                const payload = {
                    comentario: novoTexto,
                    tags: comentarioObj.tags,
                    idUsuario: userId,
                    idLocal: comentarioObj.idLocal
                };

                await fetchAPI(`/comentarios/${comentarioObj.id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                listarComentarios();
            } catch (e) { alert("Erro ao editar."); }
        }
    }

    async function excluirComentario(id) {
        if(confirm("Apagar comentário?")) {
            try {
                await fetchAPI(`/comentarios/${id}`, { method: 'DELETE' });
                listarComentarios();
            } catch (e) { alert("Erro ao excluir."); }
        }
    }

    function configurarEdicao() {
        setupEditBtn('btn-edit-name', 'user-name-input');
        setupEditBtn('btn-edit-email', 'user-email-input');
        setupEditBtn('btn-edit-pass', 'user-pass-input');
    }

    function setupEditBtn(btnId, inputId) {
        const btn = document.getElementById(btnId);
        const input = document.getElementById(inputId);
        if (!btn || !input) return;

        btn.addEventListener('click', async () => {
            if (input.disabled) {
                input.disabled = false;
                input.focus();
                btn.textContent = "Salvar";
            } else {
                input.disabled = true;
                btn.textContent = "Salvando...";
                
                try {
                    const nome = document.getElementById('user-name-input').value;
                    const email = document.getElementById('user-email-input').value;
                    const senha = document.getElementById('user-pass-input').value;

                    if(!nome || !email || !senha) throw new Error("Preencha todos os campos.");

                    const endpoint = userRole === 'proprietario' ? `/proprietarios/${userId}` : `/usuarios/${userId}`;
                    await fetchAPI(endpoint, {
                        method: 'PUT',
                        body: JSON.stringify({ nome, email, senha })
                    });
                    
                    btn.textContent = "Editar";
                    alert("Dados atualizados.");
                    localStorage.setItem('userName', nome);
                } catch (error) {
                    input.disabled = false;
                    btn.textContent = "Salvar";
                    alert(error.message);
                }
            }
        });
    }

    window.adicionarNovoLocalPerfil = function() {
        localStorage.setItem('openNovoLocal', '1');
        window.location.href = 'mapa.html';
    };

    document.getElementById('btn-logout')?.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '../html/login.html';
    });

    const btnTrocar = document.getElementById('btn-trocar-imagem');
    const modalAvatar = document.getElementById('modal-avatar');
    const btnCancelar = document.getElementById('btn-fechar-avatar');
    const grid = document.querySelector('.avatar-grid');
    const listaAvatares = [
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Felix",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Aneka",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Midnight",
        "https://api.dicebear.com/7.x/adventurer/svg?seed=Abby"
    ];

    if (btnTrocar) {
        btnTrocar.addEventListener('click', (e) => { 
            e.preventDefault(); 
            modalAvatar.classList.add('is-open'); 
            grid.innerHTML = '';
            listaAvatares.forEach(url => {
                const img = document.createElement('img');
                img.src = url;
                img.className = 'avatar-option';
                img.onclick = () => {
                    localStorage.setItem('userAvatar', url);
                    document.querySelector('.img-container').innerHTML = `<img src="${url}">`;
                    modalAvatar.classList.remove('is-open');
                };
                grid.appendChild(img);
            });
        });
    }
    
    if (btnCancelar) btnCancelar.addEventListener('click', () => modalAvatar.classList.remove('is-open'));
});