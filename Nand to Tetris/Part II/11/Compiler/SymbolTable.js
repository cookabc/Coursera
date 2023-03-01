module.exports = class SymbolTable {
  constructor() {
    this.classTable = {}
    this.subTable = {}
    this.kindIndex = {
      static: 0,
      this: 0,
      argument: 0,
      local: 0
    }
  }

  startSubroutine() {
    this.subTable = {}
    this.kindIndex.argument = 0
    this.kindIndex.local = 0
  }

  define(name, type, kind) {
    kind = kind.toLowerCase()
    if (kind === 'field') kind = 'this'
    if (kind === 'var') kind = 'local'
    if (kind === 'arg') kind = 'argument'
    if (['static', 'this'].includes(kind)) {
      this.classTable[name] = [type, kind, this.kindIndex[kind]++]
    }
    if (['argument', 'local'].includes(kind)) {
      this.subTable[name] = [type, kind, this.kindIndex[kind]++]
    }
  }

  varCount(kind) {
    kind = kind.toLowerCase()
    if (kind === 'field') kind = 'this'
    if (kind === 'var') kind = 'local'
    if (kind === 'arg') kind = 'argument'
    return this.kindIndex[kind]
  }

  kindOf(name) {
    if (this.subTable.hasOwnProperty(name)) {
      return this.subTable[name][1]
    }
    if (this.classTable.hasOwnProperty(name)) {
      return this.classTable[name][1]
    }
  }

  typeOf(name) {
    if (this.subTable.hasOwnProperty(name)) {
      return this.subTable[name][0]
    }
    if (this.classTable.hasOwnProperty(name)) {
      return this.classTable[name][0]
    }
  }

  indexOf(name) {
    if (this.subTable.hasOwnProperty(name)) {
      return this.subTable[name][2]
    }
    if (this.classTable.hasOwnProperty(name)) {
      return this.classTable[name][2]
    }
  }
}
