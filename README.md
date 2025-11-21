# 📘 Extract Texts Script — Documentação

Este documento explica como funciona o script **extract-texts**, como instalar, configurar e executar, além de como interpretar o arquivo final gerado.

---

## 📌 Objetivo

Este script varre todos os arquivos do frontend (.tsx, .jsx, .ts, .js e .html) e extrai textos que aparecem entre **tags HTML/JSX**, como:

```html
<button>Enviar agora</button>
```

O texto será capturado e organizado em um JSON agrupado por arquivo.

---

## 📁 Estrutura

```
extract-texts/
│
├── index.ts        # Script principal
└── texts-grouped.json  # Saída gerada automaticamente
```

---

## ⚙️ Instalação

O script usa somente módulos nativos do Node.js.
Nenhuma dependência é necessária.

### Requisitos:

* Node.js 18+
* PNPM ou NPM
* TSX instalado globalmente **ou** executado pelo PNPM

Para instalar o TSX globalmente:

```
pnpm add -g tsx
```

---

## 🚀 Como rodar

Dentro da pasta `/extract-texts`, execute:

```
pnpm tsx index.ts
```

Ou com npm:

```
npx tsx index.ts
```

Após rodar, você verá:

```
💾 Arquivo gerado: texts-grouped.json
Total de arquivos com texto: X
```

E o arquivo `texts-grouped.json` será criado no mesmo diretório.

---

## 📂 Configurando o diretório base

No topo do arquivo `index.ts` existe:

```ts
const BASE_DIR = path.resolve(__dirname, "../../my-project");
```

Altere esse caminho para apontar para o diretório do seu projeto.

Exemplo:

```ts
const BASE_DIR = path.resolve(__dirname, "../../my-project");
```

---

## 🔍 O que o script extrai

O script captura **somente textos visíveis dentro de tags**, ignorando:

* expressões `{algumaCoisa}`
* valores numéricos
* operadores
* strings vazias
* lixos como `= 1 && every`

### Exemplo convertido:

De:

```html
<span>Pesquisa</span>
```

Para:

```json
{
  "file": "caminho/do/arquivo",
  "texts": ["Pesquisa"]
}
```

---

## 📦 Estrutura do JSON

O arquivo final `texts-grouped.json` tem este formato:

```json
[
  {
    "file": "C:/projeto/src/componente/index.tsx",
    "texts": [
      "Enviar agora",
      "Pesquisar",
      "Salvar"
    ]
  },
  {
    "file": "C:/projeto/src/pages/home.tsx",
    "texts": ["Bem vindo"]
  }
]
```

Cada item representa **um arquivo** contendo textos encontrados.

---

## 🧠 Como funciona

### 1. `scanDir()`

Percorre todos os diretórios recursivamente.
Ignora arquivos sem extensão ou fora da lista:

```
.tsx, .ts, .jsx, .js, .html
```

### 2. `extractTexts()`

Lê cada arquivo linha por linha.

### 3. `extractTextFromLine()`

Usa um regex:

```ts
/>([^<>]{1,300})</g
```

Para capturar conteúdos entre `>` e `<`.

---

## 🛠 Possíveis melhorias futuras

* Capturar textos em atributos como `label=""`, `placeholder=""`, `alt=""`.
* Exportar CSV
* Ignorar arquivos específicos
* Detectar textos em múltiplas linhas
* Detectar strings "hardcoded" fora de JSX

---

## 📄 Licença

Este script é livre para uso, adaptação e distribuição.

---

## ✨ Autor

Feito por Luiz Gabriel.
