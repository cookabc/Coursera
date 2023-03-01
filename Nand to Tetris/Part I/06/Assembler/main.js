const fs = require("fs")
const Code = require("./code")
const Parser = require("./parser")
const SymbolTable = require("./symbol-table")

const fileName = process.argv[2]

const symbolTable = new SymbolTable()
const code = new Code()
let parser = new Parser(fileName)
let ramAddress = 16

const firstPass = (parser, symbolTable) => {
  let counter = -1
  let symbol = null
  while (parser.hasMoreCommands()) {
    parser.advance()
    if (parser.commandType() == "L") {
      symbol = parser.symbol()
      symbolTable.addEntry(symbol, counter + 1)
    } else if (parser.commandType()) {
      counter++
    }
  }
}

const secondPass = (parser, symbolTable, code) => {
  let outStream = ""
  while (parser.hasMoreCommands()) {
    parser.advance()
    if (parser.commandType() == "A") {
      const symbol = parser.symbol()
      const command = generateACommand(symbol, symbolTable)
      outStream += `${command}\n`
    } else if (parser.commandType() == "C") {
      const comp = code.comp(parser.comp())
      const dest = code.dest(parser.dest())
      const jump = code.jump(parser.jump())
      outStream += `111${comp}${dest}${jump}\n`
    }
  }
  return outStream
}

const generateACommand = (symbol, symbolTable) => {
  let binary
  if (isNaN(parseInt(symbol))) {
    if (symbolTable.contains(symbol)) {
      binary = int2Binary(symbolTable.getAddress(symbol))
    } else {
      binary = int2Binary(ramAddress)
      symbolTable.addEntry(symbol, ramAddress++)
    }
  } else {
    binary = int2Binary(symbol)
  }
  return binary
}

const int2Binary = (num) => {
  let binary = parseInt(num).toString(2)
  while (binary.length !== 16) {
    binary = `0${binary}`
  }
  return binary
}

firstPass(parser, symbolTable)
parser = new Parser(fileName)
const outStream = secondPass(parser, symbolTable, code)

const outName = fileName.split(".")[0] + ".hack"
fs.writeFile(outName, outStream, (err) => {
  if (err) {
    throw err
  }
})


