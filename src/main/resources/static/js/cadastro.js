document.addEventListener('DOMContentLoaded', () => {
    const cadastroForm = document.getElementById('cadastroForm');

    if (cadastroForm) {
        cadastroForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const nome = document.getElementById('nome').value.trim();
            const email = document.getElementById('email').value.trim();
            const senha = document.getElementById('senha').value.trim();
            
            const tipoRadio = document.querySelector('input[name="tipo"]:checked');
            const tipo = tipoRadio ? tipoRadio.value : 'usuario';

            if (!nome || !email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            const usuarios = JSON.parse(localStorage.getItem('usuariosSimulados')) || [];
            
            if (usuarios.some(u => u.email === email)) {
                alert("Email já cadastrado!");
                return;
            }

            const novoUsuario = {
                id: Date.now(),
                nome: nome,
                email: email,
                senha: senha,
                tipo: tipo
            };

            usuarios.push(novoUsuario);
            localStorage.setItem('usuariosSimulados', JSON.stringify(usuarios));

            alert("Cadastro realizado com sucesso! Faça login.");
            window.location.href = '../html/login.html';
        });
    }
});