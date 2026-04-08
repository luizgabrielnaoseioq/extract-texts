import { log } from "console";
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
    console.log(ext)
  }
  rl.close()
}

console.log(scanDir(projectRoute))
