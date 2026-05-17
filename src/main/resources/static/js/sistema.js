document.addEventListener("DOMContentLoaded", function () {
    configurarBuscaCep();
    configurarMascaraTelefone();
    configurarMascaraCelular();
    configurarMascaraCpf();
    configurarMascaraCep();
    configurarMascaraValor();
    configurarAutoExpandTextareas();
    configurarCampoCidadeCliente();
    configurarBuscaClienteModal();
    preencherClienteSelecionadoNaEdicao();
    configurarMenuMobile();
    configurarSubmenusPorClique();
});

function alternarSubmenu(id) {

    const submenuAtual = document.getElementById(id);

    if (!submenuAtual) {
        return;
    }

    const todosSubmenus = document.querySelectorAll(".submenu");

    todosSubmenus.forEach(function (submenu) {
        if (submenu.id !== id) {
            submenu.classList.remove("aberto");
        }
    });

    submenuAtual.classList.toggle("aberto");
}

function configurarSubmenusPorClique() {

    const estiloExistente = document.getElementById("submenu-click-style");

    if (estiloExistente) {
        return;
    }

    const estilo = document.createElement("style");

    estilo.id = "submenu-click-style";

    estilo.textContent = `
        .menu-group:hover .submenu:not(.aberto) {
            display: none !important;
        }

        .submenu.aberto {
            display: flex !important;
        }
    `;

    document.head.appendChild(estilo);
}

function alternarDetalhes(id) {
    const detalhesSelecionado = document.getElementById(id);
    if (!detalhesSelecionado) return;

    document.querySelectorAll(".cliente-detalhes").forEach(function (detalhe) {
        if (detalhe.id !== id) {
            detalhe.classList.remove("aberto");
        }
    });

    detalhesSelecionado.classList.toggle("aberto");
}

function toggleSenha() {
    const campoSenha = document.getElementById("senhaHash");
    const iconeSenha = document.getElementById("iconeSenha");

    if (!campoSenha || !iconeSenha) return;

    if (campoSenha.type === "password") {
        campoSenha.type = "text";
        iconeSenha.className = "bi bi-eye-slash";
    } else {
        campoSenha.type = "password";
        iconeSenha.className = "bi bi-eye";
    }
}

function configurarMascaraTelefone() {
    const camposTelefone = document.querySelectorAll("#telefone, input[name='telefone']");

    camposTelefone.forEach((campo) => {
        campo.addEventListener("input", function () {
            campo.value = aplicarMascaraTelefone(campo.value);
        });

        campo.addEventListener("blur", function () {
            campo.value = aplicarMascaraTelefone(campo.value);
        });
    });
}

function configurarMascaraCelular() {
    const camposCelular = document.querySelectorAll("#celular, input[name='celular']");

    camposCelular.forEach((campo) => {
        campo.addEventListener("input", function () {
            campo.value = aplicarMascaraTelefone(campo.value);
        });

        campo.addEventListener("blur", function () {
            campo.value = aplicarMascaraTelefone(campo.value);
        });
    });
}

function aplicarMascaraTelefone(valor) {
    let numeros = valor.replace(/\D/g, "");

    if (numeros.length > 11) {
        numeros = numeros.substring(0, 11);
    }

    if (numeros.length <= 2) {
        return numeros.length ? `(${numeros}` : "";
    }

    if (numeros.length <= 6) {
        return `(${numeros.substring(0, 2)}) ${numeros.substring(2)}`;
    }

    if (numeros.length <= 10) {
        return `(${numeros.substring(0, 2)}) ${numeros.substring(2, 6)}-${numeros.substring(6)}`;
    }

    return `(${numeros.substring(0, 2)}) ${numeros.substring(2, 7)}-${numeros.substring(7)}`;
}

function configurarMascaraCpf() {
    const camposCpf = document.querySelectorAll("#cpf, input[name='cpf']");

    camposCpf.forEach((campo) => {
        campo.addEventListener("input", function () {
            campo.value = aplicarMascaraCpf(campo.value);
        });

        campo.addEventListener("blur", function () {
            campo.value = aplicarMascaraCpf(campo.value);
        });
    });
}

function aplicarMascaraCpf(valor) {
    let numeros = valor.replace(/\D/g, "");

    if (numeros.length > 11) {
        numeros = numeros.substring(0, 11);
    }

    if (numeros.length <= 3) {
        return numeros;
    }

    if (numeros.length <= 6) {
        return `${numeros.substring(0, 3)}.${numeros.substring(3)}`;
    }

    if (numeros.length <= 9) {
        return `${numeros.substring(0, 3)}.${numeros.substring(3, 6)}.${numeros.substring(6)}`;
    }

    return `${numeros.substring(0, 3)}.${numeros.substring(3, 6)}.${numeros.substring(6, 9)}-${numeros.substring(9)}`;
}

function configurarMascaraCep() {
    const camposCep = document.querySelectorAll("#cep, input[name='cep']");

    camposCep.forEach((campo) => {
        campo.addEventListener("input", function () {
            campo.value = aplicarMascaraCep(campo.value);
        });

        campo.addEventListener("blur", function () {
            campo.value = aplicarMascaraCep(campo.value);
        });
    });
}

function aplicarMascaraCep(valor) {
    let numeros = valor.replace(/\D/g, "");

    if (numeros.length > 8) {
        numeros = numeros.substring(0, 8);
    }

    if (numeros.length <= 5) {
        return numeros;
    }

    return `${numeros.substring(0, 5)}-${numeros.substring(5)}`;
}

function configurarMascaraValor() {
    const camposValor = document.querySelectorAll("#valor, input[name='valor']");

    camposValor.forEach((campo) => {
        campo.addEventListener("input", function () {
            campo.value = aplicarMascaraValor(campo.value);
        });

        campo.addEventListener("blur", function () {
            campo.value = aplicarMascaraValor(campo.value);
        });
    });
}

function aplicarMascaraValor(valor) {
    let numeros = valor.replace(/\D/g, "");

    if (!numeros) {
        return "";
    }

    while (numeros.length < 3) {
        numeros = "0" + numeros;
    }

    const parteInteira = numeros.slice(0, -2).replace(/^0+(?=\d)/, "");
    const parteDecimal = numeros.slice(-2);

    return `${parteInteira || "0"},${parteDecimal}`;
}

function configurarBuscaCep() {
    const campoCep = document.getElementById("cep");
    if (!campoCep) return;

    campoCep.addEventListener("blur", async function () {
        const cepLimpo = campoCep.value.replace(/\D/g, "");

        if (cepLimpo.length !== 8) {
            return;
        }

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
            const dados = await response.json();

            if (dados.erro) {
                alert("CEP não encontrado.");
                return;
            }

            preencherCamposEndereco(dados);
            selecionarCidadeNoCombo(dados.localidade, dados.uf);

        } catch (error) {
            console.error("Erro ao buscar CEP:", error);
            alert("Não foi possível localizar os dados pelo CEP.");
        }
    });
}

function preencherCamposEndereco(dados) {
    const campoEndereco = document.getElementById("endereco");
    const campoBairro = document.getElementById("bairro");

    if (campoEndereco) {
        campoEndereco.value = dados.logradouro || "";
    }

    if (campoBairro) {
        campoBairro.value = dados.bairro || "";
    }
}

function normalizarTexto(texto) {
    if (!texto) return "";

    return texto
        .toString()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim()
        .toUpperCase();
}

function normalizarTextoParaBusca(texto) {
    return normalizarTexto(texto).replace(/\D/g, "") + " " + normalizarTexto(texto);
}

/* ===== CIDADE EM CAMPO ÚNICO ===== */

function configurarCampoCidadeCliente() {
    preencherBuscaCidadeSelecionada();

    const campoBusca = document.getElementById("cidadeBusca");

    if (!campoBusca) {
        return;
    }

    campoBusca.addEventListener("input", selecionarCidadePeloNome);
    campoBusca.addEventListener("change", selecionarCidadePeloNome);

    campoBusca.addEventListener("blur", function () {
        selecionarPrimeiraCidadeEncontrada();
    });

    campoBusca.addEventListener("keydown", function (evento) {
        if (evento.key === "Enter") {
            evento.preventDefault();
            selecionarPrimeiraCidadeEncontrada();
        }
    });
}

function selecionarCidadeNoCombo(localidade, uf) {
    const campoBusca = document.getElementById("cidadeBusca");
    const campoCidadeId = document.getElementById("cidade");
    const campoUf = document.getElementById("ufCliente");
    const listaCidades = document.getElementById("listaCidades");

    if (!campoBusca || !campoCidadeId || !campoUf || !listaCidades) return;

    const localidadeNormalizada = normalizarTexto(localidade);
    const ufNormalizada = normalizarTexto(uf);
    const opcoes = Array.from(listaCidades.options);

    const cidadeEncontrada = opcoes.find((opcao) => {
        const nomeCidadeNormalizado = normalizarTexto(opcao.value);
        const ufCidadeNormalizada = normalizarTexto(opcao.getAttribute("data-uf"));

        return nomeCidadeNormalizado === localidadeNormalizada &&
            ufCidadeNormalizada === ufNormalizada;
    });

    if (!cidadeEncontrada) {
        campoCidadeId.value = "";
        campoUf.value = "";
        alert(`CEP localizado, mas a cidade ${localidade}/${uf} não foi encontrada na lista. Digite manualmente.`);
        return;
    }

    campoBusca.value = cidadeEncontrada.value;
    campoCidadeId.value = cidadeEncontrada.getAttribute("data-id") || "";
    campoUf.value = cidadeEncontrada.getAttribute("data-uf") || "";
}

function atualizarUfCliente() {
    selecionarCidadePeloNome();
}

function selecionarCidadePeloNome() {
    const campoBusca = document.getElementById("cidadeBusca");
    const campoCidadeId = document.getElementById("cidade");
    const campoUf = document.getElementById("ufCliente");
    const listaCidades = document.getElementById("listaCidades");

    if (!campoBusca || !campoCidadeId || !campoUf || !listaCidades) return;

    const textoDigitado = normalizarTexto(campoBusca.value);
    const opcoes = Array.from(listaCidades.options);

    const cidadeExata = opcoes.find((opcao) => {
        return normalizarTexto(opcao.value) === textoDigitado;
    });

    if (cidadeExata) {
        campoCidadeId.value = cidadeExata.getAttribute("data-id") || "";
        campoUf.value = cidadeExata.getAttribute("data-uf") || "";
        return;
    }

    campoCidadeId.value = "";
    campoUf.value = "";
}

function selecionarPrimeiraCidadeEncontrada() {
    const campoBusca = document.getElementById("cidadeBusca");
    const campoCidadeId = document.getElementById("cidade");
    const campoUf = document.getElementById("ufCliente");
    const listaCidades = document.getElementById("listaCidades");

    if (!campoBusca || !campoCidadeId || !campoUf || !listaCidades) return;

    const textoDigitado = normalizarTexto(campoBusca.value);

    if (!textoDigitado) {
        campoCidadeId.value = "";
        campoUf.value = "";
        return;
    }

    const opcoes = Array.from(listaCidades.options);

    const cidadeEncontrada = opcoes.find((opcao) => {
        return normalizarTexto(opcao.value).startsWith(textoDigitado);
    });

    if (!cidadeEncontrada) {
        campoCidadeId.value = "";
        campoUf.value = "";
        return;
    }

    campoBusca.value = cidadeEncontrada.value;
    campoCidadeId.value = cidadeEncontrada.getAttribute("data-id") || "";
    campoUf.value = cidadeEncontrada.getAttribute("data-uf") || "";
}

function preencherBuscaCidadeSelecionada() {
    const campoBusca = document.getElementById("cidadeBusca");
    const campoCidadeId = document.getElementById("cidade");
    const campoUf = document.getElementById("ufCliente");
    const listaCidades = document.getElementById("listaCidades");

    if (!campoBusca || !campoCidadeId || !campoUf || !listaCidades || !campoCidadeId.value) {
        return;
    }

    const opcoes = Array.from(listaCidades.options);

    const cidadeSelecionada = opcoes.find((opcao) => {
        return opcao.getAttribute("data-id") === campoCidadeId.value;
    });

    if (!cidadeSelecionada) return;

    campoBusca.value = cidadeSelecionada.value;
    campoUf.value = cidadeSelecionada.getAttribute("data-uf") || "";
}

/* ===== BUSCA DE CLIENTE NO MODAL DA ORDEM ===== */

function configurarBuscaClienteModal() {
    const campoBusca = document.getElementById("buscaClienteModal");

    if (!campoBusca) {
        return;
    }

    esconderTodosClientesModal();

    campoBusca.addEventListener("keydown", buscarClientesModalPorEnter);
}

function buscarClientesModalPorEnter(evento) {
    if (evento.key !== "Enter") {
        return;
    }

    evento.preventDefault();
    filtrarClientesModal();
}

function filtrarClientesModal() {
    const inputFiltro =
        document.getElementById("buscaClienteModal") ||
        document.getElementById("filtroClienteModal");

    const linhas = document.querySelectorAll(".linha-cliente-modal");
    const linhaNenhumResultado = document.getElementById("linhaNenhumClienteEncontrado");
    const mensagem = document.getElementById("mensagemBuscaClienteModal");

    if (!inputFiltro || !linhas.length) {
        return;
    }

    const termoDigitado = inputFiltro.value.trim();

    if (!termoDigitado) {
        esconderTodosClientesModal();

        if (linhaNenhumResultado) {
            linhaNenhumResultado.style.display = "none";
        }

        if (mensagem) {
            mensagem.textContent = "DIGITE O TERMO DE BUSCA E APERTE ENTER.";
        }

        return;
    }

    const termoNormalizado = normalizarTexto(termoDigitado);
    const numerosDigitados = termoDigitado.replace(/\D/g, "");
    let totalEncontrado = 0;

    linhas.forEach((linha) => {
        const textoBusca = linha.getAttribute("data-busca") || "";
        const textoNormalizado = normalizarTexto(textoBusca);
        const numerosLinha = textoBusca.replace(/\D/g, "");

        const encontrouPorTexto = textoNormalizado.includes(termoNormalizado);
        const encontrouPorNumero = numerosDigitados.length > 0 && numerosLinha.includes(numerosDigitados);

        if (encontrouPorTexto || encontrouPorNumero) {
            linha.style.display = "";
            totalEncontrado++;
        } else {
            linha.style.display = "none";
        }
    });

    if (linhaNenhumResultado) {
        linhaNenhumResultado.style.display = totalEncontrado === 0 ? "" : "none";
    }

    if (mensagem) {
        mensagem.textContent = totalEncontrado === 1
            ? "1 CLIENTE ENCONTRADO."
            : `${totalEncontrado} CLIENTES ENCONTRADOS.`;
    }
}

function esconderTodosClientesModal() {
    document.querySelectorAll(".linha-cliente-modal").forEach((linha) => {
        linha.style.display = "none";
    });
}

function configurarAutoExpandTextareas() {
    const textareas = document.querySelectorAll(".auto-expand");

    textareas.forEach((textarea) => {
        ajustarAlturaTextarea(textarea);

        textarea.addEventListener("input", function () {
            ajustarAlturaTextarea(textarea);
        });
    });
}

function ajustarAlturaTextarea(textarea) {
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
}

function selecionarCliente(idCliente, nomeCliente) {
    const campoId = document.getElementById("idCliente");
    const campoNome = document.getElementById("nomeClienteSelecionado");

    if (campoId) {
        campoId.value = idCliente;
    }

    if (campoNome) {
        campoNome.value = nomeCliente;
        campoNome.classList.add("cliente-selecionado");
    }

    fecharModalClientes();
}

function abrirModalClientes() {
    const modal = document.getElementById("modalClientes");
    const campoBusca = document.getElementById("buscaClienteModal");
    const mensagem = document.getElementById("mensagemBuscaClienteModal");
    const linhaNenhumResultado = document.getElementById("linhaNenhumClienteEncontrado");

    if (modal) {
        modal.classList.add("aberto");
    }

    esconderTodosClientesModal();

    if (campoBusca) {
        campoBusca.value = "";
        setTimeout(() => campoBusca.focus(), 100);
    }

    if (mensagem) {
        mensagem.textContent = "DIGITE O TERMO DE BUSCA E APERTE ENTER.";
    }

    if (linhaNenhumResultado) {
        linhaNenhumResultado.style.display = "none";
    }
}

function fecharModalClientes() {
    const modal = document.getElementById("modalClientes");

    if (modal) {
        modal.classList.remove("aberto");
    }
}

function preencherClienteSelecionadoNaEdicao() {
    const campoId = document.getElementById("idCliente");
    const campoNome = document.getElementById("nomeClienteSelecionado");

    if (!campoId || !campoNome || !campoId.value) return;

    const linhas = document.querySelectorAll("#tabelaClientesModal tbody tr, .linha-cliente-modal");

    for (const linha of linhas) {
        const botaoSelecionar = linha.querySelector("button[onclick]");
        const onclickLinha = linha.getAttribute("onclick") || "";
        const onclickBotao = botaoSelecionar ? botaoSelecionar.getAttribute("onclick") || "" : "";
        const conteudoOnclick = onclickLinha + " " + onclickBotao;

        if (!conteudoOnclick.includes(`selecionarCliente(${campoId.value},`) &&
            !conteudoOnclick.includes(`selecionarCliente('${campoId.value}',`)) {
            continue;
        }

        const celulaNome = linha.querySelector("td:nth-child(2)") || linha.querySelector("td:first-child");

        if (celulaNome && celulaNome.textContent.trim()) {
            campoNome.value = celulaNome.textContent.trim();
            campoNome.classList.add("cliente-selecionado");
        }

        break;
    }
}

/* ===== MENU MOBILE HAMBURGUER - ORDEMPRO ===== */

function configurarMenuMobile() {
    const sidebar = document.querySelector(".sidebar");

    if (!sidebar) {
        return;
    }

    let botaoMobile = document.querySelector(".mobile-menu-button");

    if (!botaoMobile) {
        botaoMobile = document.createElement("button");
        botaoMobile.type = "button";
        botaoMobile.className = "mobile-menu-button";
        botaoMobile.setAttribute("aria-label", "Abrir ou fechar menu");
        botaoMobile.innerHTML = '<i class="bi bi-list"></i>';
        document.body.prepend(botaoMobile);
    }

    botaoMobile.addEventListener("click", function () {
        sidebar.classList.toggle("aberta");
        document.body.classList.toggle("menu-mobile-aberto");
    });

    document.addEventListener("click", function (evento) {
        const clicouNoMenu = sidebar.contains(evento.target);
        const clicouNoBotao = botaoMobile.contains(evento.target);

        if (!clicouNoMenu && !clicouNoBotao) {
            sidebar.classList.remove("aberta");
            document.body.classList.remove("menu-mobile-aberto");
        }
    });

    sidebar.querySelectorAll("a").forEach(function (link) {
        link.addEventListener("click", function () {
            sidebar.classList.remove("aberta");
            document.body.classList.remove("menu-mobile-aberto");
        });
    });
}

/* =========================================================
   FILTRO PERSONALIZADO - ORDENS
========================================================= */

document.addEventListener("DOMContentLoaded", function () {
    configurarFiltroPersonalizadoOrdens();
});

function configurarFiltroPersonalizadoOrdens() {

    const checkStatus = document.getElementById("checkStatus");
    const checkCliente = document.getElementById("checkCliente");
    const checkServico = document.getElementById("checkServico");

    const campoStatus = document.getElementById("campoStatus");
    const campoCliente = document.getElementById("campoCliente");
    const campoServico = document.getElementById("campoServico");

    if (!checkStatus || !checkCliente || !checkServico) {
        return;
    }

    function atualizarCampos() {

        if (campoStatus) {
            campoStatus.style.display =
                checkStatus.checked ? "flex" : "none";
        }

        if (campoCliente) {
            campoCliente.style.display =
                checkCliente.checked ? "flex" : "none";
        }

        if (campoServico) {
            campoServico.style.display =
                checkServico.checked ? "flex" : "none";
        }
    }

    checkStatus.addEventListener("change", atualizarCampos);
    checkCliente.addEventListener("change", atualizarCampos);
    checkServico.addEventListener("change", atualizarCampos);

    atualizarCampos();
}

