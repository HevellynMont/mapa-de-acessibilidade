document.addEventListener('DOMContentLoaded', () => {
    const cadastroForm = document.getElementById('cadastroForm');

    if (cadastroForm) {
        cadastroForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const nome = document.getElementById('nome').value.trim();
            const email = document.getElementById('email').value.trim();
            const senha = document.getElementById('senha').value.trim();
            const tipoRadio = document.querySelector('input[name="tipo"]:checked');
            const tipo = tipoRadio ? tipoRadio.value : 'usuario';

            console.log('=== DEBUG CADASTRO FRONTEND ===');
            console.log('Tipo selecionado:', tipo);
            console.log('Nome:', nome);
            console.log('Email:', email);

            if (!nome || !email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            try {
                const payload = {
                    nome: nome,
                    email: email,
                    senha: senha,
                    tipo: tipo
                };
                
                console.log('Payload sendo enviado:', JSON.stringify(payload, null, 2));
                
                const response = await fetch('http://localhost:8080/api/auth/cadastro', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                const data = await response.json();

                if (response.ok && data.sucesso) {
                    alert('Cadastro realizado com sucesso! Faça login.');
                    window.location.href = '../html/login.html';
                } else {
                    alert(data.mensagem || 'Erro ao cadastrar.');
                }
            } catch (error) {
                alert('Erro ao conectar com o servidor.');
                console.error(error);
            }
        });
    }
});