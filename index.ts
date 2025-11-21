import * as fs from "fs";
import * as path from "path";

const BASE_DIR = path.resolve(__dirname, "../../my-project");
const validExtensions = [".tsx", ".ts", ".jsx", ".js", '.html'];

// Resultado agrupado
const fileMap: Record<
  string,
  {
    file: string;
    texts: string[];
  }
> = {};

function scanDir(dir: string) {
  const entries = fs.readdirSync(dir, { withFileTypes: true });

  for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);

    if (entry.isDirectory()) {
      scanDir(fullPath);
      continue;
    }

    const ext = path.extname(entry.name).toLowerCase();

    if (!ext || !validExtensions.includes(ext)) {
      continue;
    }

    extractTexts(fullPath);
  }
}

function extractTexts(filePath: string) {
  const content = fs.readFileSync(filePath, "utf8");
  const lines = content.split("\n");

  for (const line of lines) {
    const found = extractTextFromLine(line);

    if (found.length > 0) {
      if (!fileMap[filePath]) {
        fileMap[filePath] = {
          file: filePath,
          texts: []
        };
      }

      fileMap[filePath].texts.push(...found);
    }
  }
}

function extractTextFromLine(line: string): string[] {
  const found: string[] = [];
  const regex = />([^<>]{1,300})</g;
  let match;

  while ((match = regex.exec(line)) !== null) {
    const raw = match[1]?.trim();

    if (!raw) continue;
    if (raw.startsWith("{") && raw.endsWith("}")) continue;
    if (raw.match(/^[{}()\[\]=<>/;,:&|!0-9\s]+$/)) continue; // ignora coisas que claramente não são texto

    found.push(raw);
  }

  return found;
}

// Iniciar busca
scanDir(BASE_DIR);

// Converter map para array final
const finalArray = Object.values(fileMap);

// Salvar JSON final
fs.writeFileSync("texts-grouped.json", JSON.stringify(finalArray, null, 2), "utf8");

console.log("\n\n💾 Arquivo gerado: texts-grouped.json");
console.log("Total de arquivos com texto:", finalArray.length);
