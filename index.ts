import * as fs from 'fs';
import * as  path from 'path';

function readDirectory(pathForRead: string) {
  const list: string[] = []
  try {
    const files = fs.readdirSync(pathForRead)

    console.log('LENDO: ', path.resolve(pathForRead))

    for (const element of files) {
      const fullPath = path.join(pathForRead, element)
      const stats = fs.statSync(fullPath)
      if (stats.isFile()) {
        // console.log('Arquivo encontrado:', fullPath);
        const ext = path.extname(fullPath)
        if (ext === '.tsx' || ext === '.jsx') {
          // console.log('Arquivo .tsx e .jsx encontrado: ',fullPath);
          list.push(fullPath)
          // console.log('Lista de como ta ficando a CARAMBOLADA',list);
        }
      } else if (stats.isDirectory()) {
        // console.log('Pasta encontrada:', fullPath);
        const result = readDirectory(fullPath)
        // console.log('Lista de como ficou a PORRA TODA', result);

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

