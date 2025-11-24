document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    if (!localStorage.getItem('usuariosSimulados')) {
        const usuariosPadrao = [
            { id: 1, nome: "Ana Proprietária", email: "ana@teste.com", senha: "123", tipo: "proprietario" },
            { id: 2, nome: "João Usuário", email: "joao@teste.com", senha: "123", tipo: "usuario" }
        ];
        localStorage.setItem('usuariosSimulados', JSON.stringify(usuariosPadrao));
    }

    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const senha = document.getElementById('senha').value.trim();

            if (!email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            realizarLoginSimulado(email, senha);
        });
    }

    function realizarLoginSimulado(email, senha) {
        const usuarios = JSON.parse(localStorage.getItem('usuariosSimulados')) || [];
        
        const usuarioEncontrado = usuarios.find(u => u.email === email && u.senha === senha);

        if (usuarioEncontrado) {
            localStorage.setItem('userId', usuarioEncontrado.id);
            localStorage.setItem('userName', usuarioEncontrado.nome);
            localStorage.setItem('userEmail', usuarioEncontrado.email);
            localStorage.setItem('userRole', usuarioEncontrado.tipo);

            alert(`Bem-vindo(a), ${usuarioEncontrado.nome}!`);
            window.location.href = '../html/mapa.html';
        } else {
            alert('Email ou senha incorretos.');
        }
    }
});