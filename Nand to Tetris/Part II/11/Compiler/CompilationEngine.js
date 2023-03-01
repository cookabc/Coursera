const SymboTable = require('./SymbolTable')

const ifTrue = 'IF_TRUE'
const ifFalse = 'IF_FALSE'
const whileCON = 'WHILE_CON'
const whileEND = 'WHILE_END'

module.exports = class CompilationEngine {
  constructor(tokenizer, writer) {
    this.tokenizer = tokenizer
    this.table = new SymboTable()
    this.writer = writer
    this.className = ''
    this.currentName = ''
    this.currentType = ''
    this.currentKind = ''
    this.whileCounter = 0
    this.ifCounter = 0
  }

  addToTable() {
    this.table.define(this.currentName, this.currentType, this.currentKind)
  }

  checkIdentifier() {
    if (this.tokenizer.tokenType() === 'IDENTIFIER') {
      this.currentName = this.tokenizer.identifier()
      return true
    } else {
      return false
    }
  }

  checkKeyword(k) {
    return this.tokenizer.tokenType() === 'KEYWORD' && this.tokenizer.token() === k
  }

  checkAndWriteType() {
    if (
      ['INT', 'CHAR', 'BOOLEAN'].includes(this.tokenizer.keyword()) ||
      this.tokenizer.tokenType() === 'IDENTIFIER' ||
      (this.tokenizer.tokenType() === 'KEYWORD' && this.tokenizer.token() === 'void')
    ) {
      this.currentType = this.tokenizer.token()
      return true
    } else {
      return false
    }
  }

  compileClass() {
    this.tokenizer.advance()
    if (this.tokenizer.keyword() === 'CLASS') {
      // write class name
      this.tokenizer.advance()
      if (this.checkIdentifier) {
        this.className = this.tokenizer.identifier()
      } else {
        console.warn('illegal class name identifier')
        return
      }
      // write {
      this.tokenizer.advance()
      if (this.tokenizer.symbol() !== '{') {
        console.warn('no openning { for class')
        return
      }
      // parse potential classVarDec
      this.tokenizer.advance()
      while (['STATIC', 'FIELD'].includes(this.tokenizer.keyword())) {
        this.compileClassVarDec()
        this.tokenizer.advance()
      }
      // parse potential subroutineDec
      while (['CONSTRUCTOR', 'FUNCTION', 'METHOD'].includes(this.tokenizer.keyword())) {
        this.compileSubroutine()
        this.tokenizer.advance()
      }
      // write }
      if (this.tokenizer.symbol() !== '}') {
        console.warn('no closing } for class')
        return
      }
      if (this.tokenizer.hasMoreTokens()) {
        console.warn('addtional tokens after closing }')
      }
    } else {
      console.warn('does not start with class')
      return
    }
  }

  compileClassVarDec() {
    this.currentKind = this.tokenizer.keyword()
    // match type
    this.tokenizer.advance()
    if (!this.checkAndWriteType()) {
      console.warn('illegal type for class var dec')
      return
    }
    // match varName
    this.tokenizer.advance()
    if (this.checkIdentifier()) {
      this.addToTable()
    } else {
      console.warn('illegal classVar identifier')
      return
    }
    // match potential ", varName" part
    this.tokenizer.advance()
    while (this.tokenizer.symbol() === ',') {
      this.tokenizer.advance()
      if (this.checkIdentifier()) {
        this.addToTable()
      } else {
        console.warn('illegal classVar identifier')
        return
      }
      this.tokenizer.advance()
    }
    // match ;
    if (this.tokenizer.symbol() !== ';') {
      console.warn('no ending ;')
      return
    }
  }

  compileSubroutine() {
    // clear the previous subroutine symbol table
    this.table.startSubroutine()
    // already know that the current token start with constructor, function or method
    let subRoutineKind = this.tokenizer.keyword()
    // match return type
    this.tokenizer.advance()
    if (!this.checkAndWriteType()) {
      console.warn('Illegal type name for subroutine')
      return
    }
    let currentSubName = null
    // match subroutine identifier
    this.tokenizer.advance()
    if (this.checkIdentifier()) {
      currentSubName = this.className + '.' + this.currentName
    } else {
      console.warn('illegal subroutine name')
      return
    }
    if (subRoutineKind === 'METHOD') {
      this.table.define('this', this.className, 'ARG')
    }
    // match parameter list
    this.tokenizer.advance()
    if (this.tokenizer.symbol() === '(') {
      this.compileParameterList()
    } else {
      console.warn('no ( after function name')
      return
    }
    // match the closing ) for the paramater list
    if (this.tokenizer.symbol() !== ')') {
      consolse.warn('no ) after function name')
      return
    }
    // match subroutine body
    this.tokenizer.advance()
    if (this.tokenizer.symbol() === '{') {
      this.tokenizer.advance()
      while (this.tokenizer.tokenType() === 'KEYWORD' && this.tokenizer.token() === 'var') {
        this.compileVarDec()
        this.tokenizer.advance()
      }
    } else {
      console.warn('no { after function parameters')
      return
    }
    this.writer.writeFunction(currentSubName, this.table.varCount('VAR'))
    if (subRoutineKind === 'CONSTRUCTOR') {
      // allocate space in constructor
      let numOfFields = this.table.varCount('FIELD')
      if (numOfFields > 0) {
        this.writer.writePush('constant', numOfFields)
      }
      this.writer.writeCall('Memory.alloc', 1)
      this.writer.writePop('pointer', 0)
    } else if (subRoutineKind === 'METHOD') {
      // set up this pointer in method
      this.writer.writePush('argument', 0)
      this.writer.writePop('pointer', 0)
    }
    this.compileStatements()
    // match }
    if (this.tokenizer.symbol() !== '}') {
      console.warn('no } found to close subroutine call')
      console.warn('current token is: ' + this.tokenizer.token())
    }
  }

  compileParameterList() {
    this.currentKind = 'ARG'
    let numberOfArgs = 0
    // write type
    this.tokenizer.advance()
    if (this.checkAndWriteType()) {
      // match varName
      this.tokenizer.advance()
      if (this.checkIdentifier()) {
        this.addToTable()
        numberOfArgs++
      } else {
        console.warn('illegal identifier in parameter list')
        return -1
      }
      // match other arguments
      this.tokenizer.advance()
      while (this.tokenizer.symbol() === ',') {
        this.tokenizer.advance()
        if (!this.checkAndWriteType()) {
          console.warn('illegal type name')
          return -1
        }
        this.tokenizer.advance()
        if (this.checkIdentifier()) {
          this.addToTable()
          numberOfArgs++
        } else {
          console.warn('illegal identifier name')
          return
        }
        this.tokenizer.advance()
      }
    }
    return numberOfArgs
  }

  compileVarDec() {
    this.currentKind = 'VAR'
    // check type
    this.tokenizer.advance()
    if (!this.checkAndWriteType()) {
      console.warn('illegal type for var')
      return
    }
    // check varName
    this.tokenizer.advance()
    if (this.checkIdentifier()) {
      this.addToTable()
    } else {
      console.warn('illegal identifier for var')
      return
    }
    this.tokenizer.advance()
    while (this.tokenizer.symbol() === ',') {
      this.tokenizer.advance()
      if (this.checkIdentifier()) {
        this.addToTable()
      } else {
        console.warn('illegal identifier for var')
        return
      }
      this.tokenizer.advance()
    }
    if (this.tokenizer.symbol() !== ';') {
      console.warn("varDec doesn't end with ;")
      return
    }
  }

  compileStatements() {
    while (this.tokenizer.tokenType() === 'KEYWORD') {
      const keywordType = this.tokenizer.keyword()
      // compileIf needs to do one token look ahead to check "else",
      // so no more advance here.
      switch (keywordType) {
        case 'LET':
          this.compileLet()
          this.tokenizer.advance()
          break
        case 'IF':
          this.compileIf()
          break
        case 'WHILE':
          this.compileWhile()
          this.tokenizer.advance()
          break
        case 'DO':
          this.compileDo()
          this.tokenizer.advance()
          break
        case 'RETURN':
          this.compileReturn()
          this.tokenizer.advance()
          break
        default:
          console.warn('illegal statement')
          return
      }
    }
  }

  compileDo() {
    this.tokenizer.advance()
    // Before call compileSubRoutineCall, first check if the current token is valid identifier. Then advance again and check if the it is . or (
    if (this.checkIdentifier()) {
      let firstHalf = this.currentName
      this.tokenizer.advance()
      if (['.', '('].includes(this.tokenizer.symbol())) {
        this.compileSubRoutineCall(firstHalf)
      } else {
        console.warn('Not valid subroutine call')
        return
      }
    } else {
      console.warn('Not a valid identifier for do statement')
      return
    }
    this.tokenizer.advance()
    if (this.tokenizer.symbol() !== ';') {
      console.warn('No closing ;')
      return
    }
    this.writer.writePop('temp', 0)
  }

  compileLet() {
    this.tokenizer.advance()
    if (!this.checkIdentifier()) {
      console.warn('Illegal identifier')
      return
    }
    let varName = this.currentName
    let isArray = false
    let kind = this.table.kindOf(varName)
    let index = this.table.indexOf(varName)
    this.tokenizer.advance()
    if (this.tokenizer.symbol() === '[') {
      this.tokenizer.advance()
      this.compileExpression()
      if (this.tokenizer.symbol() !== ']') {
        console.warn('1, No closing ] for the array expression')
      }
      isArray = true
      // the top of stack should be the index
      this.writer.writePush(kind, index)
      this.writer.writeArithmetic('add')
      this.writer.writePop('temp', 2)
      this.tokenizer.advance()
    }
    // if has [], advance and next should be =
    if (this.tokenizer.symbol() !== '=') {
      console.warn('No = found')
      return
    }
    this.tokenizer.advance()
    this.compileExpression()
    if (isArray) {
      this.writer.writePush('temp', 2)
      this.writer.writePop('pointer', 1)
      this.writer.writePop('that', 0)
    } else {
      this.writer.writePop(kind, index)
    }
    // No need to advance because compileExpression does one token look ahead
    if (this.tokenizer.symbol() !== ';') {
      console.warn('No ; found at the end of statement')
      return
    }
  }

  compileWhile() {
    let localCounter = this.whileCounter++
    this.writer.writeLabel(whileCON, localCounter)
    this.tokenizer.advance()
    if (this.tokenizer.symbol() !== '(') {
      console.warn('No ( in while statement')
      return
    }
    this.tokenizer.advance()
    this.compileExpression()
    if (this.tokenizer.symbol() !== ')') {
      console.warn('No ) in while statement')
      return
    }
    // negate the top of stack
    this.writer.writeArithmetic('not')
    this.tokenizer.advance()
    if (this.tokenizer.symbol() !== '{') {
      console.warn('No { in while statement')
      return
    }
    this.writer.writeIf(whileEND, localCounter)
    this.tokenizer.advance()
    this.compileStatements()
    this.writer.writeGoto(whileCON, localCounter)
    if (this.tokenizer.symbol() !== '}') {
      console.warn('No } in while statement')
      return
    }
    this.writer.writeLabel(whileEND, localCounter)
  }

  compileReturn() {
    this.tokenizer.advance()
    // if the following is not ; then try to parse argument
    if (this.tokenizer.symbol() !== ';') {
      this.compileExpression()
      // after the expresison, it should end with ;
      if (this.tokenizer.symbol() !== ';') {
        console.warn('return statement not ending with ;')
      }
    } else {
      this.writer.writePush('constant', 0)
    }
    this.writer.writeReturn()
  }

  compileIf() {
    let localCounter = this.ifCounter++
    this.tokenizer.advance()
    if (this.tokenizer.symbol() !== '(') {
      console.warn('No openning ( for if statement')
      return
    }
    this.tokenizer.advance()
    this.compileExpression()
    if (this.tokenizer.symbol() !== ')') {
      console.warn('No closing ) for if statement')
      return
    }
    this.writer.writeArithmetic('not')
    this.tokenizer.advance()
    if (this.tokenizer.symbol() !== '{') {
      console.warn('No { for if statement')
      return
    }
    this.writer.writeIf(ifFalse, localCounter)
    this.tokenizer.advance()
    this.compileStatements()
    this.writer.writeGoto(ifTrue, localCounter)
    if (this.tokenizer.symbol() !== '}') {
      console.warn('No } for if statement')
      console.warn('the current symbol is ' + tokenizer.token())
      return
    }
    this.writer.writeLabel(ifFalse, localCounter)
    this.tokenizer.advance()
    if (this.checkKeyword('else')) {
      this.tokenizer.advance()
      if (this.tokenizer.symbol() !== '{') {
        console.warn('No { for else statment')
        return
      }
      this.tokenizer.advance()
      this.compileStatements()
      if (this.tokenizer.symbol() !== '}') {
        console.warn('No } for if statement')
        return
      }
      this.tokenizer.advance()
    }
    this.writer.writeLabel(ifTrue, localCounter)
  }

  compileExpression() {
    this.compileTerm()
    while (['+', '-', '*', '/', '&', '|', '<', '>', '='].includes(this.tokenizer.symbol())) {
      let localSymbol = this.tokenizer.symbol()
      this.tokenizer.advance()
      this.compileTerm()
      // write op vm code here
      if (localSymbol === '+') {
        this.writer.writeArithmetic('add')
      } else if (localSymbol === '-') {
        this.writer.writeArithmetic('sub')
      } else if (localSymbol === '*') {
        this.writer.writeArithmetic('call Math.multiply 2')
      } else if (localSymbol === '/') {
        this.writer.writeArithmetic('call Math.divide 2')
      } else if (localSymbol === '&') {
        this.writer.writeArithmetic('and')
      } else if (localSymbol === '|') {
        this.writer.writeArithmetic('or')
      } else if (localSymbol === '<') {
        this.writer.writeArithmetic('lt')
      } else if (localSymbol === '>') {
        this.writer.writeArithmetic('gt')
      } else if (localSymbol === '=') {
        this.writer.writeArithmetic('eq')
      }
      // no advance here, because compileTerm needs to do one token look ahead
    }
  }

  compileTerm() {
    if (this.checkIdentifier()) {
      let firstHalf = this.currentName
      this.tokenizer.advance()
      if (this.tokenizer.symbol() === '[') {
        this.writer.writePush(this.table.kindOf(firstHalf), this.table.indexOf(firstHalf))
        this.tokenizer.advance()
        this.compileExpression()
        if (this.tokenizer.symbol() !== ']') {
          console.warn('2, No closing ] for the array expression')
        }
        this.writer.writeArithmetic('add')
        this.writer.writePop('pointer', 1)
        this.writer.writePush('that', 0)
        this.tokenizer.advance()
      } else if (['(', '.'].includes(this.tokenizer.symbol())) {
        this.compileSubRoutineCall(firstHalf)
        this.tokenizer.advance()
      } else {
        // if doesn't match [, (, or ., it is a normal identifier
        this.writer.writePush(this.table.kindOf(firstHalf), this.table.indexOf(firstHalf))
      }
    } else if (this.tokenizer.tokenType() === 'INT_CONST') {
      this.writer.writePush('constant', this.tokenizer.intVal())
      this.tokenizer.advance()
    } else if (this.tokenizer.tokenType() === 'STRING_CONST') {
      let strLiteral = this.tokenizer.stringVal()
      strLiteral = [...strLiteral]
      this.writer.writePush('constant', strLiteral.length)
      this.writer.writeCall('String.new', 1)
      strLiteral.forEach(s => {
        let code = s.charCodeAt()
        this.writer.writePush('constant', code)
        this.writer.writeCall('String.appendChar', 2)
      })
      this.tokenizer.advance()
    } else if (this.checkKeyword('true') || this.checkKeyword('false') || this.checkKeyword('null') || this.checkKeyword('this')) {
      if (this.checkKeyword('null') || this.checkKeyword('false')) {
        this.writer.writePush('constant', 0)
      } else if (this.checkKeyword('true')) {
        this.writer.writePush('constant', 1)
        this.writer.writeArithmetic('neg')
      } else if (this.checkKeyword('this')) {
        this.writer.writePush('pointer', 0)
      }
      this.tokenizer.advance()
    } else if (['-', '~'].includes(this.tokenizer.symbol())) {
      this.tokenizer.advance()
      let localSymbol = this.tokenizer.symbol()
      this.compileTerm()
      if (localSymbol === '-') {
        this.writer.writeArithmetic('neg')
      } else {
        this.writer.writeArithmetic('not')
      }
    } else if (this.tokenizer.tokenType() === 'SYMBOL') {
      if (this.tokenizer.symbol() === '(') {
        this.tokenizer.advance()
        this.compileExpression()
        if (this.tokenizer.symbol() === ')') {
          this.tokenizer.advance()
        } else {
          console.warn('no closing bracket for term')
        }
      }
    } else {
      console.warn('illegal varName: ' + this.tokenizer.token())
      return
    }
  }

  compileSubRoutineCall(firstHalf) {
    const classRegx = '^[A-Z].*'
    let isClass = !!firstHalf.match(classRegx)
    let fullSubName = null
    let numOfArgs = 0
    if (this.tokenizer.symbol() === '(') {
      fullSubName = this.className + '.' + firstHalf
      this.tokenizer.advance()
      this.writer.writePush('pointer', 0)
      numOfArgs = this.compileExpressionList(isClass)
      if (this.tokenizer.symbol() !== ')') {
        console.warn('No closing ) for the expressionlist')
        return
      }
    } else {
      this.tokenizer.advance()
      if (this.checkIdentifier()) {
        if (isClass) {
          // class function, don't push this pointer
          fullSubName = firstHalf + '.' + this.currentName
        } else {
          // firstHalf must be a variable defined in the symbol table
          fullSubName = this.table.typeOf(firstHalf) + '.' + this.currentName
          // push b's address
          this.writer.writePush(this.table.kindOf(firstHalf), this.table.indexOf(firstHalf))
        }
      } else {
        console.warn('illegal identifier for subroutine call')
        return
      }
      this.tokenizer.advance()
      if (this.tokenizer.symbol() !== '(') {
        console.warn('Expecting a open bracket in subroutine call')
        return
      }
      this.tokenizer.advance()
      numOfArgs = this.compileExpressionList(isClass)
      if (this.tokenizer.symbol() !== ')') {
        console.warn('No closing ) for the expressionlist')
        return
      }
    }
    if (fullSubName != null) this.writer.writeCall(fullSubName, numOfArgs)
  }

  compileExpressionList(isClass) {
    let argCounter = 1
    if (isClass) argCounter = 0
    if (this.tokenizer.symbol() !== ')') {
      this.compileExpression()
      argCounter++
      // because compileExpression did 1 token look ahead, no advance here
      while (this.tokenizer.symbol() === ',') {
        this.tokenizer.advance()
        this.compileExpression()
        argCounter++
      }
    }
    return argCounter
  }
}
