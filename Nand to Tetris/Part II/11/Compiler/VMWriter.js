const fs = require('fs')

module.exports = class VMWriter {
  constructor(outFile) {
    this.output = ''
    this.outFile = outFile
  }

  writePush(segment, index) {
    this.output += `push ${segment} ${index}\n`
  }

  writePop(segment, index) {
    this.output += `pop ${segment} ${index}\n`
  }

  writeArithmetic(command) {
    this.output += `${command}\n`
  }

  writeLabel(label, counter) {
    this.output += `label ${label}${counter}\n`
  }

  writeGoto(label, counter) {
    this.output += `goto ${label}${counter}\n`
  }

  writeIf(label, counter) {
    this.output += `if-goto ${label}${counter}\n`
  }

  writeCall(name, nArgs) {
    this.output += `call ${name} ${nArgs}\n`
  }

  writeFunction(name, nLocals) {
    this.output += `function ${name} ${nLocals}\n`
  }

  writeReturn() {
    this.output += `return\n`
  }

  writeFile() {
    fs.writeFile(this.outFile, this.output, err => {
      if (err) throw err
    })
  }
}
