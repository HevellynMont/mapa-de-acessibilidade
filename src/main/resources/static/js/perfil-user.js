// -------- CARREGAR DADOS DO USUÁRIO LOGADO --------

/**
 * Busca os dados do usuário logado do localStorage
 */
function obterUsuarioLogado() {
    const userId = localStorage.getItem('userId');
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    const userRole = localStorage.getItem('userRole');

    if (!userId || userRole !== 'usuario') {
        alert('Você precisa estar logado como usuário para acessar esta página!');
        window.location.href = 'login.html';
        return null;
    }

    return {
        id: userId,
        nome: userName,
        email: userEmail,
        role: userRole
    };
}

/**
 * Busca os comentários do usuário no backend
 */
async function buscarComentariosUsuario(usuarioId) {
    try {
        const response = await fetch(`/api/comentarios/usuario/${usuarioId}`);
        if (response.ok) {
            return await response.json();
        }
        return [];
    } catch (error) {
        console.error('Erro ao buscar comentários:', error);
        return [];
    }
}

/**
 * Busca todas as tags disponíveis
 */
async function buscarTags() {
    try {
        console.log('Fazendo requisição para /api/tags...');
        const response = await fetch('/api/tags');
        console.log('Resposta recebida:', response.status, response.statusText);
        
        if (response.ok) {
            const tags = await response.json();
            console.log('Tags parseadas:', tags);
            return tags;
        } else {
            console.error('Erro ao buscar tags. Status:', response.status);
            return [];
        }
    } catch (error) {
        console.error('Erro ao buscar tags:', error);
        alert('Erro ao carregar as tags de acessibilidade. Verifique o console para mais detalhes.');
        return [];
    }
}

/**
 * Deleta um comentário
 */
async function deletarComentario(comentarioId) {
    if (!confirm('Tem certeza que deseja excluir este comentário?')) {
        return false;
    }
    
    try {
        const response = await fetch(`/api/comentarios/${comentarioId}`, {
            method: 'DELETE'
        });
        
        if (response.ok || response.status === 204) {
            alert('Comentário excluído com sucesso!');
            return true;
        } else {
            alert('Erro ao excluir comentário.');
            return false;
        }
    } catch (error) {
        console.error('Erro ao deletar comentário:', error);
        alert('Erro ao excluir comentário.');
        return false;
    }
}

/**
 * Atualiza um comentário
 */
async function atualizarComentario(comentarioId, descricao, nota, tagIds) {
    try {
        const response = await fetch(`/api/comentarios/${comentarioId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                descricao: descricao,
                nota: nota
            })
        });
        
        if (response.ok) {
            // Se houver tags, atualizar também
            if (tagIds && tagIds.length > 0) {
                await atualizarTagsComentario(comentarioId, tagIds);
            }
            return true;
        } else {
            return false;
        }
    } catch (error) {
        console.error('Erro ao atualizar comentário:', error);
        return false;
    }
}

/**
 * Atualiza as tags de um comentário
 */
async function atualizarTagsComentario(comentarioId, tagIds) {
    try {
        const response = await fetch(`/api/comentarios/${comentarioId}/tags`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(tagIds)
        });
        return response.ok;
    } catch (error) {
        console.error('Erro ao atualizar tags:', error);
        return false;
    }
}

/**
 * Busca os locais marcados pelo usuário (simulado por enquanto)
 */
async function buscarLocaisUsuario(usuarioId) {
    // TODO: Implementar endpoint no backend quando estiver pronto
    // Por enquanto retorna array vazio
    return [];
}

/**
 * Função que atualiza a página com os dados de um usuário.
 */
async function carregarPerfil(usuario) {
    
    // 1. Preencher informações simples (Nome e E-mail)
    document.getElementById('user-name').textContent = usuario.nome;
    document.getElementById('user-email').textContent = usuario.email;

    // 2. Buscar e preencher Comentários
    console.log('Buscando comentários do usuário:', usuario.id);
    const comentarios = await buscarComentariosUsuario(usuario.id);
    console.log('Comentários recebidos:', comentarios);
    const commentsContainer = document.getElementById('comments-container');
    
    if (comentarios.length === 0) {
        commentsContainer.innerHTML = `
            <h2>Comentários:</h2>
            <p class="empty-state">Nenhum comentário ainda.</p>
        `;
    } else {
        const comentariosHtml = comentarios.map(comentario => {
            const tagsHtml = comentario.tagsComentadas && comentario.tagsComentadas.length > 0
                ? comentario.tagsComentadas.map(tag => `<span class="tag-badge">${tag.nome}</span>`).join('')
                : '<span class="tag-badge">Sem tags</span>';
            
            return `
                <article class="comment-card" data-comentario-id="${comentario.id}">
                    <div class="comment-header">
                        <h4>${comentario.local ? comentario.local.nome : 'Local não especificado'}</h4>
                        <div class="comment-actions">
                            <button class="btn-editar" data-comentario-id="${comentario.id}" title="Editar">
                                <i class="fa-solid fa-edit"></i>
                            </button>
                            <button class="btn-deletar" data-comentario-id="${comentario.id}" title="Excluir">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="comment-author">
                        <small>${usuario.nome} - Nota: ${comentario.nota}/5</small>
                    </div>
                    <p>${comentario.descricao}</p>
                    <div class="comment-tags">
                        ${tagsHtml}
                    </div>
                </article>
            `;
        }).join('');

        commentsContainer.innerHTML = '<h2>Comentários:</h2>' + comentariosHtml;
        
        // Adicionar event listeners aos botões de editar e deletar
        document.querySelectorAll('.btn-editar').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const comentarioId = e.currentTarget.getAttribute('data-comentario-id');
                await abrirModalEdicao(comentarioId, comentarios);
            });
        });
        
        document.querySelectorAll('.btn-deletar').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const comentarioId = e.currentTarget.getAttribute('data-comentario-id');
                const sucesso = await deletarComentario(comentarioId);
                if (sucesso) {
                    // Recarregar perfil
                    await carregarPerfil(usuario);
                }
            });
        });
    }

    // 3. Buscar e preencher Locais Marcados
    const locais = await buscarLocaisUsuario(usuario.id);
    const locationsContainer = document.getElementById('locations-container');

    if (locais.length === 0) {
        locationsContainer.innerHTML = `
            <h2>Locais marcados como acessível:</h2>
            <p class="empty-state">Nenhum local marcado ainda.</p>
        `;
    } else {
        const locaisHtml = locais.map(local => {
            return `
                <li><a href="mapa.html?local=${local.id}">${local.nome}</a></li>
            `;
        }).join('');

        locationsContainer.innerHTML = `
            <h2>Locais marcados como acessível:</h2>
            <ul class="locations-list">${locaisHtml}</ul>
        `;
    }
}

/**
 * Abre o modal de edição com os dados do comentário
 */
async function abrirModalEdicao(comentarioId, comentarios) {
    const comentario = comentarios.find(c => c.id == comentarioId);
    if (!comentario) {
        console.error('Comentário não encontrado:', comentarioId);
        return;
    }
    
    console.log('Abrindo modal para comentário:', comentario);
    
    // Preencher campos do formulário
    document.getElementById('edit-comentario-id').value = comentario.id;
    document.getElementById('edit-local-nome').value = comentario.local ? comentario.local.nome : 'Local não especificado';
    document.getElementById('edit-descricao').value = comentario.descricao;
    document.getElementById('edit-nota').value = comentario.nota;
    
    // Buscar e exibir tags
    console.log('Buscando tags...');
    const todasTags = await buscarTags();
    console.log('Tags recebidas:', todasTags);
    
    const tagsContainer = document.getElementById('edit-tags-container');
    const tagsComentadasIds = comentario.tagsComentadas ? comentario.tagsComentadas.map(t => t.id) : [];
    
    if (todasTags.length === 0) {
        tagsContainer.innerHTML = '<p style="color: #888; font-style: italic;">Nenhuma tag disponível</p>';
    } else {
        tagsContainer.innerHTML = todasTags.map(tag => {
            const checked = tagsComentadasIds.includes(tag.id) ? 'checked' : '';
            return `
                <label class="tag-checkbox">
                    <input type="checkbox" name="tags" value="${tag.id}" ${checked}>
                    <span>${tag.nome}</span>
                </label>
            `;
        }).join('');
    }
    
    // Abrir modal
    document.getElementById('modal-editar-comentario').classList.add('is-open');
}

// -------- QUANDO A PÁGINA CARREGAR --------

document.addEventListener('DOMContentLoaded', async () => {
    
    // Verificar se o usuário está logado e carregar seus dados
    const usuarioLogado = obterUsuarioLogado();
    if (!usuarioLogado) {
        return; // A função obterUsuarioLogado já redireciona se não estiver logado
    }

    // Carregar o perfil do usuário
    await carregarPerfil(usuarioLogado);

    // --- LÓGICA DO MODAL DE AVATAR ---
    const btnTrocarImagem = document.getElementById('btn-trocar-imagem');
    const modalAvatar = document.getElementById('modal-avatar');
    const btnCancelar = document.getElementById('btn-cancelar-avatar');
    const avatarOptions = document.querySelectorAll('.avatar-option');
    const perfilImgContainer = document.querySelector('.profile-picture .img-container');

    // --- LÓGICA DE EDIÇÃO DE DADOS (NOME/EMAIL) ---
    
    const modalEdicaoUser = document.getElementById('modal-edicao-usuario');
    const formEdicaoUser = document.getElementById('form-edicao-usuario');
    const inputEdicaoUser = document.getElementById('input-edicao-usuario');
    const labelEdicaoUser = document.getElementById('label-edicao-usuario');
    const btnCancelarEdicaoUser = document.getElementById('btn-cancelar-edicao-usuario');
    
    const btnTrocarNome = document.getElementById('btn-trocar-nome');
    const btnTrocarEmail = document.getElementById('btn-trocar-email');

    // Função para abrir o modal configurado
    function abrirModalEdicaoUser(campo, valorAtual) {
        labelEdicaoUser.textContent = `Novo ${campo}:`;
        inputEdicaoUser.value = valorAtual;
        // Define o 'name' do input para sabermos o que estamos editando na hora do submit
        inputEdicaoUser.name = campo === 'Nome' ? 'nome' : 'email'; 
        inputEdicaoUser.type = campo === 'E-mail' ? 'email' : 'text';
        
        modalEdicaoUser.classList.add('is-open');
    }

    function fecharModalEdicaoUser() {
        modalEdicaoUser.classList.remove('is-open');
    }

    // Botão Trocar Nome
    if (btnTrocarNome) {
        btnTrocarNome.addEventListener('click', (e) => {
            e.preventDefault();
            // Pega o nome atual da tela ou do localStorage
            const nomeAtual = localStorage.getItem('userName') || document.getElementById('user-name').textContent;
            abrirModalEdicaoUser('Nome', nomeAtual);
        });
    }

    // Botão Trocar Email
    if (btnTrocarEmail) {
        btnTrocarEmail.addEventListener('click', (e) => {
            e.preventDefault();
            const emailAtual = localStorage.getItem('userEmail') || document.getElementById('user-email').textContent;
            abrirModalEdicaoUser('E-mail', emailAtual);
        });
    }

    // Botão Cancelar
    if (btnCancelarEdicaoUser) {
        btnCancelarEdicaoUser.addEventListener('click', fecharModalEdicaoUser);
    }

    // Submit do Formulário
    if (formEdicaoUser) {
        formEdicaoUser.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const campo = inputEdicaoUser.name; // 'nome' ou 'email'
            const novoValor = inputEdicaoUser.value.trim();
            
            if (!novoValor) {
                alert("O campo não pode ficar vazio.");
                return;
            }

            // Monta o objeto para enviar ao backend
            const dados = {};
            dados[campo] = novoValor;

            const sucesso = await atualizarDadosUsuario(usuarioLogado.id, dados);

            if (sucesso) {
                // Atualiza a interface
                if (campo === 'nome') {
                    document.getElementById('user-name').textContent = novoValor;
                    // Atualiza o objeto usuarioLogado local também para manter consistência
                    usuarioLogado.nome = novoValor; 
                } else {
                    document.getElementById('user-email').textContent = novoValor;
                    usuarioLogado.email = novoValor;
                }
                fecharModalEdicaoUser();
            }
        });
    }

    // Fechar ao clicar fora
    if (modalEdicaoUser) {
        modalEdicaoUser.addEventListener('click', (e) => {
            if (e.target === modalEdicaoUser) {
                fecharModalEdicaoUser();
            }
        });
    }

    // Função para ABRIR o modal
    function abrirModal(e) {
        e.preventDefault();
        modalAvatar.classList.add('is-open');
    }

    // Função para FECHAR o modal
    function fecharModal() {
        modalAvatar.classList.remove('is-open');
    }

    // Ligar os botões
    if (btnTrocarImagem) {
        btnTrocarImagem.addEventListener('click', abrirModal);
    }
    
    if (btnCancelar) {
        btnCancelar.addEventListener('click', fecharModal);
    }

    // Fechar o modal clicando FORA dele
    if (modalAvatar) {
        modalAvatar.addEventListener('click', (e) => {
            if (e.target === modalAvatar) {
                fecharModal();
            }
        });
    }

    // Lógica para ESCOLHER um avatar
    avatarOptions.forEach(img => {
        img.addEventListener('click', () => {
            const novaImgSrc = img.src;
            perfilImgContainer.innerHTML = `<img src="${novaImgSrc}" alt="Avatar do usuário">`;
            fecharModal();
        });
    });

    // --- LÓGICA DO BOTÃO DESLOGAR ---
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            // Limpar localStorage
            localStorage.removeItem('userId');
            localStorage.removeItem('userName');
            localStorage.removeItem('userEmail');
            localStorage.removeItem('userRole');
            // Redirecionar para login
            window.location.href = 'login.html';
        });
    }

    // --- LÓGICA DO MODAL DE EDIÇÃO DE COMENTÁRIO ---
    const modalEditarComentario = document.getElementById('modal-editar-comentario');
    const btnCancelarEdicao = document.getElementById('btn-cancelar-edicao');
    const formEditarComentario = document.getElementById('form-editar-comentario');

    // Fechar modal de edição
    if (btnCancelarEdicao) {
        btnCancelarEdicao.addEventListener('click', () => {
            modalEditarComentario.classList.remove('is-open');
        });
    }

    // Fechar modal clicando fora
    if (modalEditarComentario) {
        modalEditarComentario.addEventListener('click', (e) => {
            if (e.target === modalEditarComentario) {
                modalEditarComentario.classList.remove('is-open');
            }
        });
    }

    // Submeter formulário de edição
    if (formEditarComentario) {
        formEditarComentario.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const comentarioId = document.getElementById('edit-comentario-id').value;
            const descricao = document.getElementById('edit-descricao').value;
            const nota = parseInt(document.getElementById('edit-nota').value);
            
            // Obter tags selecionadas
            const tagsCheckboxes = document.querySelectorAll('#edit-tags-container input[name="tags"]:checked');
            const tagIds = Array.from(tagsCheckboxes).map(cb => parseInt(cb.value));
            
            // Atualizar comentário
            const sucesso = await atualizarComentario(comentarioId, descricao, nota, tagIds);
            
            if (sucesso) {
                alert('Comentário atualizado com sucesso!');
                modalEditarComentario.classList.remove('is-open');
                // Recarregar perfil
                await carregarPerfil(usuarioLogado);
            } else {
                alert('Erro ao atualizar comentário.');
            }
        });
    }
});
