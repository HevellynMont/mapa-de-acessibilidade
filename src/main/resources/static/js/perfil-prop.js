// -------- CARREGAR DADOS DO PROPRIETÁRIO LOGADO --------

/**
 * Busca os dados do proprietário logado do localStorage
 */
function obterProprietarioLogado() {
    const userId = localStorage.getItem('userId');
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
    const userRole = localStorage.getItem('userRole');

    if (!userId || userRole !== 'proprietario') {
        alert('Você precisa estar logado como proprietário para acessar esta página!');
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
 * Busca os locais do proprietário no backend
 */
async function buscarLocaisProprietario(proprietarioId) {
    try {
        // ASSUMINDO QUE O BACKEND ESTÁ EM localhost:8080
        const response = await fetch(`http://localhost:8080/api/locais/proprietario/${proprietarioId}`);

        if (response.status === 204) {
            return [];
        }

        if (response.ok) {
            return await response.json();
        }

        console.error('Erro ao buscar locais. Status:', response.status);
        return [];
    } catch (error) {
        console.error('Erro ao buscar locais:', error);
        return [];
    }
}


/**
 * Busca um local por ID
 */
async function buscarLocalPorId(id) {
    const url = `http://localhost:8080/api/locais/${id}`;  // CORRIGIR URL

    const resposta = await fetch(url);
    if (!resposta.ok) {
        throw new Error("Erro ao buscar local por ID");
    }

    return await resposta.json();
}

/**
 * Deleta um local
 */
async function deletarLocal(localId) {
    if (!confirm('Tem certeza que deseja excluir este local?')) {
        return false;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/locais/${localId}`, {
            method: 'DELETE'
        });

        if (response.ok || response.status === 204) {
            alert('Local excluído com sucesso!');
            return true;
        } else {
            alert('Erro ao excluir local.');
            return false;
        }
    } catch (error) {
        console.error('Erro ao deletar local:', error);
        alert('Erro ao excluir local.');
        return false;
    }
}

/**
 * FUNÇÃO NOVA: Atualiza o nome ou e-mail do proprietário no backend e localStorage.
 */
async function atualizarDadosProprietario(proprietarioId, novosDados) {
    try {
        const url = `http://localhost:8080/api/proprietarios/${proprietarioId}`;
        const response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novosDados)
        });

        if (response.ok) {
            // Tenta ler o JSON apenas se for sucesso
            const data = await response.json(); 
            // Atualiza o localStorage com os novos dados
            if (novosDados.nome) {
                localStorage.setItem('userName', novosDados.nome);
            }
            if (novosDados.email) {
                localStorage.setItem('userEmail', novosDados.email);
            }
            alert('Dados atualizados com sucesso!');
            return true;
        } else {
            // Se falhou (4xx ou 5xx), tenta ler a mensagem de erro do corpo
            let mensagemErro = 'Erro ao atualizar dados. Status: ' + response.status;
            try {
                const errorData = await response.json();
                mensagemErro = errorData.mensagem || errorData.error || mensagemErro;
            } catch (e) {
                // Se não conseguir ler JSON, usa a mensagem padrão
            }
            alert(mensagemErro);
            return false;
        }
    } catch (error) {
        // Erro de rede ou CORS
        console.error('Erro de conexão/rede:', error);
        alert('Erro ao conectar com o servidor para atualizar dados. Verifique o console para detalhes (CORS?).');
        return false;
    }
}

/**
 * Função que atualiza a página com os dados de um proprietário.
 */
// ... (código anterior da função carregarPerfil)

/**
 * Função que atualiza a página com os dados de um proprietário.
 */
async function carregarPerfil(proprietario) {
    // 1. Preencher informações simples (Nome e E-mail)
    document.getElementById('user-name').textContent = proprietario.nome;
    document.getElementById('user-email').textContent = proprietario.email;

    // 2. Buscar e preencher Locais Adicionados
    const locais = await buscarLocaisProprietario(proprietario.id);
    const addLocationsContainer = document.getElementById('add-locations-container');
    const locaisSectionTitle = '<h2>Locais adicionados:</h2>';
    
    // FUNÇÃO AUXILIAR para adicionar o listener ao botão de Adicionar Novo Local
    // É importante que essa função seja chamada após o innerHTML ser definido
   function setupAddLocalButton() {
    const btnAdicionarLocal = document.getElementById('btn-adicionar-local');
    if (btnAdicionarLocal) {
        btnAdicionarLocal.addEventListener('click', () => {
            // Redireciona para o mapa, passando o parâmetro 'addLocal=true'
            window.location.href = 'mapa.html?addLocal=true'; 
        });
    }
}


    if (locais.length === 0) {
        addLocationsContainer.innerHTML = `
            ${locaisSectionTitle}
            <p class="empty-state">Nenhum local cadastrado ainda.</p>
            <button class="add-location-btn" id="btn-adicionar-local">
                <span class="add-icon">+</span>
                <span>Adicionar novos locais</span>
            </button>
        `;
        // LIGA O LISTENER IMEDIATAMENTE APÓS INJETAR O HTML
        setupAddLocalButton(); 
    } else {
        const locaisHtml = locais.map(local => {
            // ... (código para gerar o HTML do local-card)
            const tagsHtml = local.tags && local.tags.length > 0
                ? local.tags.map(tag => `<span class="tag-badge">${tag.tagNome}</span>`).join('')
                : '<span class="tag-badge">Sem tags</span>';

            return `
                <article class="local-card" data-local-id="${local.id}">
                    <div class="local-header">
                        <h4>${local.nome}</h4>
                        <div class="local-actions">
                            <button class="btn-editar" data-local-id="${local.id}" title="Editar">
                                <i class="fa-solid fa-edit"></i>
                            </button>
                            <button class="btn-deletar" data-local-id="${local.id}" title="Excluir">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <p class="local-endereco"><i class="fa-solid fa-location-dot"></i> ${local.endereco}</p>
                    <p class="local-descricao">${local.descricao || 'Sem descrição.'}</p>
                    <div class="local-tags">
                        ${tagsHtml}
                    </div>
                </article>
            `;
        }).join('');

        addLocationsContainer.innerHTML = `
            ${locaisSectionTitle}
            ${locaisHtml}
            <button class="add-location-btn" id="btn-adicionar-local">
                <span class="add-icon">+</span>
                <span>Adicionar novos locais</span>
            </button>
        `;
        
        // LIGA O LISTENER IMEDIATAMENTE APÓS INJETAR O HTML
        setupAddLocalButton();

        // Adicionar event listeners aos botões de editar e deletar (mantido, pois funcionam)
        document.querySelectorAll('.btn-editar').forEach(btn => {
           btn.addEventListener('click', async (e) => {
                const localId = e.currentTarget.getAttribute('data-local-id');
                const localData = await buscarLocalPorId(localId);

                if (!localData) {
                    alert("Erro: local não encontrado!");
                    return;
                }

                // Preenche os inputs
                document.getElementById('editar-local-nome').value = localData.nome;
                document.getElementById('editar-local-descricao').value = localData.descricao || "";
                document.getElementById('editar-local-endereco').value = localData.endereco || "";
                

                // Guarda o ID dentro do form
                document.getElementById('form-editar-local').setAttribute("data-id", localId);

                // Abre o modal
                document.getElementById('modal-editar-local').classList.add('is-open');
            });

        });

        document.querySelectorAll('.btn-deletar').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                // ... (lógica de deletar)
                const localId = e.currentTarget.getAttribute('data-local-id');
                const sucesso = await deletarLocal(localId);
                if (sucesso) {
                    // Recarregar perfil para atualizar lista de locais
                    const propAtualizado = obterProprietarioLogado();
                    await carregarPerfil(propAtualizado);
                }
            });
        });
    }

    // REMOVIDO: O bloco de código que ligava o listener do botão aqui, pois ele falhava.
    
    // 3. Limpar seções de comentários... (mantido)
    document.getElementById('comments-container').innerHTML = '<h2>Comentários:</h2><p class="empty-state">Proprietários não fazem comentários.</p>';
    document.getElementById('locations-container').innerHTML = '<h2>Locais marcados como acessível:</h2><p class="empty-state">Proprietários não marcam locais.</p>';
}

async function atualizarLocal(localId, dados) {
    console.log("→ Chamando atualizarLocal", localId, dados);
    
    try {
        const response = await fetch(`http://localhost:8080/api/locais/${localId}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        const texto = await response.text();
        console.log("Status:", response.status);
        console.log("Resposta do servidor:", texto);

        return response.ok;
    } catch(err) {
        console.error("Erro ao atualizar local:", err);
        return false;
 
    }
}

/**
 * Atualiza dados do usuário (Nome ou Email)
 */
async function atualizarDadosUsuario(usuarioId, novosDados) {
    try {
        // Ajuste a URL conforme sua API. Geralmente é /api/usuarios/{id}
        const response = await fetch(`http://localhost:8080/api/usuarios/${usuarioId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(novosDados)
        });

        if (response.ok) {
            // Atualiza localStorage para refletir a mudança imediatamente
            if (novosDados.nome) localStorage.setItem('userName', novosDados.nome);
            if (novosDados.email) localStorage.setItem('userEmail', novosDados.email);
            
            alert('Dados atualizados com sucesso!');
            return true;
        } else {
            alert('Erro ao atualizar dados. Verifique se o e-mail já não está em uso.');
            return false;
        }
    } catch (error) {
        console.error('Erro ao atualizar usuário:', error);
        alert('Erro de conexão com o servidor.');
        return false;
    }
}

document.addEventListener('DOMContentLoaded', async () => {

    // 1. Verificar se o proprietário está logado e carregar seus dados
    let proprietarioLogado = obterProprietarioLogado();
    if (!proprietarioLogado) {
        return;
    }

    // Carregar o perfil do proprietário
    await carregarPerfil(proprietarioLogado);

    // --- LÓGICA DE EDIÇÃO DE NOME E E-MAIL ---
    
    const btnTrocarNome = document.getElementById('btn-trocar-nome');
    const btnTrocarEmail = document.getElementById('btn-trocar-email');
    const modalEdicao = document.getElementById('modal-edicao');
    const formEdicao = document.getElementById('form-edicao');
    const inputEdicao = document.getElementById('input-edicao');
    const labelEdicao = document.getElementById('label-edicao');
    const btnCancelarEdicao = document.getElementById('btn-cancelar-edicao');

    // Abre o modal de edição
    function abrirModalEdicao(campo, valorAtual) {
        labelEdicao.textContent = `Novo ${campo}:`;
        inputEdicao.value = valorAtual;
        inputEdicao.name = campo === 'Nome' ? 'nome' : 'email'; // Define o nome do campo para o submit
        inputEdicao.type = campo === 'E-mail' ? 'email' : 'text';
        modalEdicao.classList.add('is-open');
    }
    
    // Fecha o modal de edição
    function fecharModalEdicao() {
        modalEdicao.classList.remove('is-open');
    }

    // Event Listeners para Abrir o Modal de Edição
    if (btnTrocarNome) {
        btnTrocarNome.addEventListener('click', (e) => {
            e.preventDefault();
            abrirModalEdicao('Nome', proprietarioLogado.nome);
        });
    }

    if (btnTrocarEmail) {
        btnTrocarEmail.addEventListener('click', (e) => {
            e.preventDefault();
            abrirModalEdicao('E-mail', proprietarioLogado.email);
        });
    }
    
    if (btnCancelarEdicao) {
        btnCancelarEdicao.addEventListener('click', fecharModalEdicao);
    }

    // Lógica para o SUBMIT do formulário de edição
    if (formEdicao) {
        formEdicao.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const campo = inputEdicao.name;
            const novoValor = inputEdicao.value.trim();
            
            if (novoValor === '') {
                alert(`O campo ${campo} não pode ser vazio.`);
                return;
            }

            const dadosParaAtualizar = {};
            dadosParaAtualizar[campo] = novoValor;
            
            const sucesso = await atualizarDadosProprietario(proprietarioLogado.id, dadosParaAtualizar);
            
            if (sucesso) {
                fecharModalEdicao();
                // Recarrega o perfil com os dados atualizados do localStorage
                proprietarioLogado = obterProprietarioLogado(); 
                await carregarPerfil(proprietarioLogado); 
            }
        });
    }
    
    // Fechar o modal clicando FORA dele
    if (modalEdicao) {
        modalEdicao.addEventListener('click', (e) => {
            if (e.target === modalEdicao) {
                fecharModalEdicao();
            }
        });
    }

    // --- LÓGICA DO MODAL DE AVATAR (MANTIDA) ---

    const btnTrocarImagem = document.getElementById('btn-trocar-imagem');
    const modalAvatar = document.getElementById('modal-avatar');
    const btnCancelarAvatar = document.getElementById('btn-cancelar-avatar'); // Corrigido ID
    const avatarOptions = document.querySelectorAll('.avatar-option');
    const perfilImgContainer = document.querySelector('.profile-picture .img-container');

    // Função para ABRIR o modal
    function abrirModalAvatar(e) {
        e.preventDefault();
        modalAvatar.classList.add('is-open');
    }

    // Função para FECHAR o modal
    function fecharModalAvatar() {
        modalAvatar.classList.remove('is-open');
    }

    // Ligar os botões
    if (btnTrocarImagem) {
        btnTrocarImagem.addEventListener('click', abrirModalAvatar);
    }
    
    if (btnCancelarAvatar) {
        btnCancelarAvatar.addEventListener('click', fecharModalAvatar);
    }

    // Fechar o modal clicando FORA dele
    if (modalAvatar) {
        modalAvatar.addEventListener('click', (e) => {
            if (e.target === modalAvatar) {
                fecharModalAvatar();
            }
        });
    }

    // Lógica para ESCOLHER um avatar
    avatarOptions.forEach(img => {
        img.addEventListener('click', () => {
            const novaImgSrc = img.src;
            perfilImgContainer.innerHTML = `<img src="${novaImgSrc}" alt="Avatar do proprietário">`;
            fecharModalAvatar();
        });
    });

    // --- LÓGICA DO BOTÃO DESLOGAR (MANTIDA) ---
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


const modalEditarLocal = document.getElementById('modal-editar-local');
const formEditarLocal = document.getElementById('form-editar-local');
const btnCancelarEdicaoLocal = document.getElementById('cancelar-edicao-local');

// Fechar modal
btnCancelarEdicaoLocal.addEventListener('click', () => {
    modalEditarLocal.classList.remove('is-open');
});



// Submit
    formEditarLocal.addEventListener('submit', async (e) => {
        e.preventDefault();

        const localId = formEditarLocal.getAttribute("data-id");

        const dadosAtualizados = {
            nome: document.getElementById('editar-local-nome').value.trim(),
            descricao: document.getElementById('editar-local-descricao').value.trim(),
            endereco: document.getElementById('editar-local-endereco').value.trim()
            
        };

        dadosAtualizados.tagIds = localData.tags.map(tag => tag.id);

        const sucesso = await atualizarLocal(localId, dadosAtualizados);

        if (sucesso) {
            alert("Local atualizado com sucesso!");

            modalEditarLocal.classList.remove('is-open');

            // Recarregar lista de locais
            const proprietarioLogado = obterProprietarioLogado();
            await carregarPerfil(proprietarioLogado);
        } else {
            alert("Erro ao atualizar o local!");
        }
});

    
});