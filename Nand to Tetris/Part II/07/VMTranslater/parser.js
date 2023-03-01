const arithmeticCmds = ['add', 'sub', 'neg', 'eq', 'gt', 'lt', 'and', 'or', 'not']
const unaryCmds = ['pop', 'push', 'label', 'if', 'goto', 'function', 'call']

module.exports = class Parser {

  constructor(commands) {
    this.commands = commands
    this.currentCommand = null
    this.currentCommandType = null
    this.argument1 = null
    this.argument2 = null
  }

  hasMoreCommands() {
    return this.commands.length > 0
  }

  advance() {
    let cmd = this.removeComments(this.commands.shift()).trim()
    while ((cmd === '' || cmd.startsWith('//')) && this.commands.length > 0) {
      cmd = this.removeComments(this.commands.shift()).trim()
    }
    this.currentCommand = cmd
    const segs = cmd.split(' ')
    if (segs.length > 3) {
      throw 'Too much arguments!'
    }
    if (arithmeticCmds.includes(segs[0])) {
      this.currentCommandType = 'ARITHMETIC'
      this.argument1 = segs[0]
    } else if (segs[0] === 'return') {
      this.currentCommandType = 'RETURN'
      this.argument1 = segs[0]
    } else if (unaryCmds.includes(segs[0])) {
      this.currentCommandType = segs[0].toUpperCase()
      this.argument1 = segs[1]
    } else if (cmd === '') {
      this.currentCommandType = ''
      this.argument1 = ''
    } else {
      throw 'Unknown Command Type'
    }
    if (['CALL', 'FUNCTION', 'POP', 'PUSH'].includes(this.currentCommandType)) {
      this.argument2 = segs[2]
    }
  }

  commandType() {
    return this.currentCommandType
  }

  arg1() {
    if (this.currentCommandType !== 'RETURN') {
      return this.argument1
    } else {
      throw 'Can not get arg1 from a RETURN type command!'
    }
  }

  arg2() {
    if (['CALL', 'FUNCTION', 'POP', 'PUSH'].includes(this.currentCommandType)) {
      return parseInt(this.argument2)
    } else {
      throw 'Can not get arg2!'
    }
  }

  removeComments(str) {
    if(str.includes('//')) {
      str = str.slice(0, str.indexOf('//'))
    }
    return str
  }
}
