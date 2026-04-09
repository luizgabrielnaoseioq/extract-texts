import * as fs from "fs";
import * as path from "path";
import * as readline from 'readline/promises'

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})
const validExtensions = [".tsx", ".ts", ".jsx", ".js", '.html'];
const projectRoute = await rl.question('Where is the project route? ')

console.log(`Scanning this route: ${projectRoute}`)

function scanDir(dir) {
  const entries = fs.readdirSync(dir, { withFileTypes: true })
  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name)
    if (entry.isDirectory()) {
      scanDir(fullPath)
      continue
    }
    const ext = path.extname(entry.name).toLowerCase()
    if (!ext || !validExtensions.includes(ext)) {
      continue
    }
    const line = readLine(fullPath)
    console.log(line)
  }
  rl.close()
}

function readLine(file) {
  try {
    const data = fs.readFileSync(file, 'utf8')
    const linhas = data.split(/\r?\n/)
    linhas.forEach(() => {
      console.log(`Linha ${index + 1}: ${linha}`)
    })
  } catch (error) {
    console.log("Erro ao ler arquivo: ", error)
  }
}

console.log(scanDir(projectRoute))
