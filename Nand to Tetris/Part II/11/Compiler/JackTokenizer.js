const fs = require('fs')

const keywordPattern = '^class$|^constructor$|^function$|^method$|^field$|^static$|^var$|^int$|^char$|^boolean$|^void$|^true$|^false$|^null$|^this$|^let$|^do$|^if$|^else$|^while$|^return$'
const symbolPattern = '[\\&\\*\\+\\(\\)\\.\\/\\,\\-\\]\\;\\~\\}\\|\\{\\>\\=\\[\\<]'
const intPattern = '^[0-9]+$'
const stringPattern = '"[^"\n]*"'
const identifierPattern = '[\\w_]+'

const combinedRegex = (...regexes) => new RegExp('(' + regexes.map(regex => new RegExp(regex).source).join(')|(') + ')', 'g')
const tokenPattern = combinedRegex(keywordPattern, symbolPattern, intPattern, stringPattern, identifierPattern)

const keywordList = [
  'class',
  'constructor',
  'function',
  'method',
  'field',
  'static',
  'var',
  'int',
  'char',
  'boolean',
  'void',
  'true',
  'false',
  'null',
  'this',
  'let',
  'do',
  'if',
  'else',
  'while',
  'return'
]
const symbolList = ['{', '}', '(', ')', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~', '&lt;', '&gt;', '&amp;']

module.exports = class JackTokenizer {
  constructor(inputFile) {
    const inputStream = fs.readFileSync(inputFile, 'utf-8')
    this.tokens = this.generateTokens(inputStream)
    this.currentToken = null
  }

  removeInlineComments(strIn) {
    let position = strIn.indexOf('//')
    if (position != -1) {
      strIn = strIn.substring(0, position)
    }
    return strIn
  }

  removeBlockComments(strIn) {
    let startIndex = strIn.indexOf('/*')
    if (startIndex == -1) return strIn

    let result = strIn
    let endIndex = strIn.indexOf('*/')

    while (startIndex != -1) {
      if (endIndex == -1) {
        return strIn.substring(0, startIndex - 1)
      }
      result = result.substring(0, startIndex) + result.substring(endIndex + 2)
      startIndex = result.indexOf('/*')
      endIndex = result.indexOf('*/')
    }
    return result
  }

  generateTokens(inputStream) {
    let tokens = []
    const tokenLines = this.removeBlockComments(inputStream).split('\n')
    tokenLines.forEach(i => {
      let tokenLine = this.removeInlineComments(i)
      if (tokenLine) {
        const matches = tokenLine.match(tokenPattern)
        if (matches) {
          tokens = [...tokens, ...matches]
        }
      }
    })
    return tokens
  }

  hasMoreTokens() {
    return this.tokens.length > 0
  }

  advance() {
    if (this.hasMoreTokens()) {
      this.currentToken = this.tokens.shift()
    }
  }

  token() {
    return this.currentToken
  }

  tokenType() {
    let tokenType
    if (keywordList.includes(this.currentToken)) {
      tokenType = 'KEYWORD'
    } else if (symbolList.includes(this.currentToken)) {
      tokenType = 'SYMBOL'
    } else if (this.currentToken.match(intPattern)) {
      tokenType = 'INT_CONST'
    } else if (this.currentToken.match(stringPattern)) {
      tokenType = 'STRING_CONST'
    } else if (this.currentToken.match(identifierPattern)) {
      tokenType = 'IDENTIFIER'
    }
    return tokenType
  }

  keyword() {
    if (this.tokenType() === 'KEYWORD' && keywordList.includes(this.currentToken)) {
      return this.currentToken.toUpperCase()
    }
  }

  symbol() {
    if (this.tokenType() === 'SYMBOL') {
      return this.currentToken
    }
  }

  identifier() {
    if (this.tokenType() === 'IDENTIFIER') {
      return this.currentToken
    }
  }

  intVal() {
    if (this.tokenType() === 'INT_CONST') {
      return parseInt(this.currentToken)
    }
  }

  stringVal() {
    if (this.tokenType() === 'STRING_CONST') {
      return this.currentToken.replace(/\"/g, '')
    }
  }
}
