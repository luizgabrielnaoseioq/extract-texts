import * as fs from 'fs';
import * as  path from 'path';

function readDirectory(pathForRead: string) {
  const IGNORE_FOLDERS = ['node_modules', '.git', 'dist', 'build']
  const list: string[] = []
  try {
    const files = fs.readdirSync(pathForRead)
    for (const element of files) {
      const fullPath = path.join(pathForRead, element)
      const stats = fs.statSync(fullPath)
      if (stats.isFile()) {
        const ext = path.extname(fullPath)
        if (ext === '.tsx' || ext === '.jsx') {
          list.push(fullPath)
        }
      } else if (stats.isDirectory()) {
        const folderName = path.basename(fullPath)
        if (IGNORE_FOLDERS.includes(folderName)) {
          continue
        }
        const result = readDirectory(fullPath)
        list.push(...result)
      }
    }
    return list
  } catch (error) {
    console.error('Erro ao acessar o caminho: ', pathForRead, error);
    return []
  }
}

const pathToRead = '../../frontend-bpf'

console.log(
  readDirectory(pathToRead)
);

