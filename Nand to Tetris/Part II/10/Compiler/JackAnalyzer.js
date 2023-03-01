const fs = require("fs")

const JackTokenizer = require("./JackTokenizer")
const CompilationEngine = require("./CompilationEngine")

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
  if (inputFile.endsWith(".jack")) {
    const tokenizer = new JackTokenizer(inputFile)
    const compilationEngine = new CompilationEngine(tokenizer)
    compilationEngine.compileClass()
    writeOutFile(inputFile.split(".jack").join("_new.xml"), compilationEngine.output.join("\n"))
  }
}

function writeOutFile(name, data) {
  fs.writeFile(name, data, (err) => {
    if (err) throw err
  })
}
