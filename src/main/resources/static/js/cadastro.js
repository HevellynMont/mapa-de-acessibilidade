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

            if (!nome || !email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            const endpoint = tipo === 'proprietario' ? '/proprietarios' : '/usuarios';
            
            const payload = {
                nome: nome,
                email: email,
                senha: senha
            };

            try {
                await fetchAPI(endpoint, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });

                alert("Cadastro realizado com sucesso! Faça login.");
                window.location.href = '../html/login.html';
            } catch (error) {
                alert("Erro ao cadastrar: " + error.message);
            }
        });
    }
});