// Endereço da API do backend
const API_URL = "http://localhost:8080/produtos";

// Quando a página abrir, carrega os produtos
carregarProdutos();

// Quando clicar no botão "Adicionar", chama a função
document.getElementById("adicionar").addEventListener("click", adicionarProduto);

// Função para buscar todos os produtos e mostrar na tabela
async function carregarProdutos() {
  // Busca os produtos do backend
  const resposta = await fetch(API_URL);
  const listaDeProdutos = await resposta.json();

  // Pega o corpo da tabela
  const corpoTabela = document.querySelector("tbody");
  corpoTabela.innerHTML = "";

  // Para cada produto, cria uma linha na tabela
  for (const produto of listaDeProdutos) {
    const linha = document.createElement("tr");

    linha.innerHTML = `
      <td>${produto.id}</td>
      <td>${produto.nome}</td>
      <td>R$ ${produto.preco.toFixed(2)}</td>
      <td><button onclick="deletar(${produto.id})">Excluir</button></td>
    `;

    corpoTabela.appendChild(linha);
  }
}

// Função para adicionar um novo produto
async function adicionarProduto() {
  // Pega os valores digitados
  const nome = document.getElementById("nome").value;
  const preco = document.getElementById("preco").value;

  // Se os campos estiverem vazios, mostra um alerta
  if (!nome || !preco) {
    alert("Por favor, preencha todos os campos!");
    return;
  }

  // Cria o produto no backend
  await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nome: nome, preco: preco })
  });

  // Limpa os campos
  document.getElementById("nome").value = "";
  document.getElementById("preco").value = "";

  // Atualiza a tabela
  carregarProdutos();
}

// Função para excluir um produto pelo ID
async function deletar(id) {
  await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  carregarProdutos();
}
