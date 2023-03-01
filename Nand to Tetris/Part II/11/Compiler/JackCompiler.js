const fs = require('fs')

const JackTokenizer = require('./JackTokenizer')
const CompilationEngine = require('./CompilationEngine')
const VMWrite = require('./VMWriter')

const fileName = process.argv[2]
const isDirectory = fs.lstatSync(fileName).isDirectory()

if (isDirectory) {
  fs.readdir(fileName, (err, files) => {
    if (err) throw err
    files.forEach(file => {
      processFileData(`${fileName}/${file}`)
    })
  })
} else {
  processFileData(fileName)
}

function processFileData(inputFile) {
  if (inputFile.endsWith('.jack')) {
    const writer = new VMWrite(inputFile.split('.jack').join('.vm'))
    const tokenizer = new JackTokenizer(inputFile)
    const compilationEngine = new CompilationEngine(tokenizer, writer)
    compilationEngine.compileClass()
    writer.writeFile()
  }
}
