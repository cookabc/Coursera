const fs = require('fs')
const path = require('path')

const callSymbols = ['LCL', 'ARG', 'THIS', 'THAT']
const returnSymbols = ['THAT', 'THIS', 'ARG', 'LCL']

module.exports = class Writer {
  constructor(outputFile) {
    this.jumpIndex = 0
    this.labelCount = 0
    this.fileName = ''
    this.outStream = ''
    this.outputFile = this.setOutputFile(outputFile)
  }

  setOutputFile(fileName) {
    if (fs.lstatSync(fileName).isDirectory()) {
      return `${path.resolve(fileName)}/${path.basename(fileName)}.asm`
    } else {
      return `${path.resolve(fileName).replace('.vm', '.asm')}`
    }
  }

  setFileName(fileName) {
    this.fileName = path.basename(fileName)
  }

  writeInit() {
    this.outStream += '@256\n'
      + 'D=A\n'
      + '@SP\n'
      + 'M=D\n'
    this.writeCall('Sys.init', 0)
  }

  writeArithmetic(command) {
    let output
    switch (command) {
      case 'add':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'D=M\n'
          + 'A=A-1\n'
          + 'M=M+D\n'
        break
      case 'sub':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'D=M\n'
          + 'A=A-1\n'
          + 'M=M-D\n'
        break
      case 'neg':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'M=-M\n'
          + '@SP\n'
          + 'M=M+1\n'
        break
      case 'eq':
        output = this.jumpTemplate('JEQ')
        break
      case 'gt':
        output = this.jumpTemplate('JGT')
        break
      case 'lt':
        output = this.jumpTemplate('JLT')
        break
      case 'and':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'D=M\n'
          + 'A=A-1\n'
          + 'M=M&D\n'
        break
      case 'or':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'D=M\n'
          + 'A=A-1\n'
          + 'M=M|D\n'
        break
      case 'not':
        output = '@SP\n'
          + 'AM=M-1\n'
          + 'M=!M\n'
          + '@SP\n'
          + 'M=M+1\n'
        break
    }
    this.outStream += output
  }

  writePushPop(command, segment, index) {
    let output
    switch (segment) {
      case 'constant':
        output = `@${index}\n`
          + 'D=A\n'
          + '@SP\n'
          + 'A=M\n'
          + 'M=D\n'
          + '@SP\n'
          + 'M=M+1\n'
        break
      case 'static':
        output = {
          PUSH: `@${this.fileName}${index}\n`
            + 'D=M\n'
            + '@SP\n'
            + 'A=M\n'
            + 'M=D\n'
            + '@SP\n'
            + 'M=M+1\n',
          POP: '@SP\n'
            + 'AM=M-1\n'
            + 'D=M\n'
            + `@${this.fileName}${index}\n`
            + 'M=D\n'
        }[command]
        break
      case 'pointer':
        if (index === 0) {
          segment = 'THIS'
        } else if (index === 1) {
          segment = 'THAT'
        }
        output = {
          PUSH: `@${segment}\n`
            + 'D=M\n'
            + '@SP\n'
            + 'A=M\n'
            + 'M=D\n'
            + '@SP\n'
            + 'M=M+1\n',
          POP: `@${segment}\n`
            + 'D=A\n'
            + '@R13\n'
            + 'M=D\n'
            + '@SP\n'
            + 'AM=M-1\n'
            + 'D=M\n'
            + '@R13\n'
            + 'A=M\n'
            + 'M=D\n'
        }[command]
        break
      case 'temp':
        output = {
          PUSH: this.pushTemplate('@R5\n', index, false),
          POP: this.popTemplate('@R5\n', index, false)
        }[command]
        break
      default:
        let str
        if (segment === 'argument') {
          str = '@ARG\n'
        } else if (segment === 'local') {
          str = '@LCL\n'
        } else {
          str = `@${segment.toUpperCase()}\n`
        }
        output = {
          PUSH: this.pushTemplate(str, index, true),
          POP: this.popTemplate(str, index, true)
        }[command]
    }
    this.outStream += output
  }

  writeLabel(label) {
    this.outStream += `(${label})\n`
  }

  writeGoto(label) {
    this.outStream += `@${label}\n` + '0;JMP\n'
  }

  writeIf(label) {
    this.outStream += '@SP\n'
      + 'AM=M-1\n'
      + 'D=M\n'
      + `@${label}\n`
      + 'D;JNE\n'
  }

  writeCall(functionName, numArgs) {
    let output = `@${functionName}_RETURN_${this.labelCount}\n`
      + 'D=A\n'
      + '@SP\n'
      + 'A=M\n'
      + 'M=D\n'
      + '@SP\n'
      + 'M=M+1\n'
    callSymbols.forEach(symbol => {
      output += `@${symbol}\n`
        + 'D=M\n'
        + '@SP\n'
        + 'A=M\n'
        + 'M=D\n'
        + '@SP\n'
        + 'M=M+1\n'
    })
    output += `@${numArgs}\n`
      + 'D=A\n'
      + '@5\n'
      + 'D=A+D\n'
      + '@SP\n'
      + 'D=M-D\n'
      + '@ARG\n'
      + 'M=D\n'
      + '@SP\n'
      + 'D=M\n'
      + '@LCL\n'
      + 'M=D\n'
      + `@${functionName}\n`
      + '0;JMP\n'
      + `(${functionName}_RETURN_${this.labelCount})\n`
    this.labelCount++
    this.outStream += output
  }

  writeReturn() {
    let tmpStr = ''
    returnSymbols.forEach(symbol => {
      tmpStr += '@R13\n'
        + 'D=M-1\n'
        + 'AM=D\n'
        + 'D=M\n'
        + `@${symbol}\n`
        + 'M=D\n'
    })
    this.outStream += '@LCL\n'
      + 'D=M\n'
      + '@R13\n'
      + 'M=D\n'
      + '@5\n'
      + 'A=D-A\n'
      + 'D=M\n'
      + '@R14\n'
      + 'M=D\n'
      + '@SP\n'
      + 'AM=M-1\n'
      + 'D=M\n'
      + '@ARG\n'
      + 'A=M\n'
      + 'M=D\n'
      + '@ARG\n'
      + 'D=M+1\n'
      + '@SP\n'
      + 'M=D\n'
      + tmpStr
      + '@R14\n'
      + 'A=M\n'
      + '0;JMP\n'
  }

  writeFunction(functionName, numLocals) {
    this.outStream += `(${functionName})\n`
    for (let i = 0; i < numLocals; i++) {
      this.writePushPop('PUSH', 'constant', 0)
    }
  }

  jumpTemplate(type) {
    let output = '@SP\n'
      + 'AM=M-1\n'
      + 'D=M\n'
      + 'A=A-1\n'
      + 'D=M-D\n'
      + `@TRUE${this.jumpIndex}\n`
      + `D;${type}\n`
      + '@SP\n'
      + 'A=M-1\n'
      + 'M=0\n'
      + `@CONTINUE${this.jumpIndex}\n`
      + '0;JMP\n'
      + `(TRUE${this.jumpIndex})\n`
      + '@SP\n'
      + 'A=M-1\n'
      + 'M=-1\n'
      + `(CONTINUE${this.jumpIndex})\n`
    return output
  }

  popTemplate(str, index, flag) {
    let output = str
      + `${flag ? 'D=M' : 'D=A'}\n`
      + '@' + index + '\n'
      + 'D=D+A\n'
      + '@R13\n'
      + 'M=D\n'
      + '@SP\n'
      + 'AM=M-1\n'
      + 'D=M\n'
      + '@R13\n'
      + 'A=M\n'
      + 'M=D\n'

    return output
  }

  pushTemplate(str, index, flag) {
    let output = str
      + `${flag ? 'D=M' : 'D=A'}\n`
      + '@' + index + '\n'
      + 'A=D+A\n'
      + 'D=M\n'
      + '@SP\n'
      + 'A=M\n'
      + 'M=D\n'
      + '@SP\n'
      + 'M=M+1\n'

    return output
  }

  writeFile() {
    const outputPath = this.outputFile
    fs.writeFile(outputPath, this.outStream, (err) => {
      if (err) {
        throw err
      }
    })
  }
}
