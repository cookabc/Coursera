module.exports = class CompilationEngine {
  constructor(tokenizer) {
    this.tokenizer = tokenizer
    this.output = []
    this.spacing = ""
  }

  writeTag(word, type) {
    this.output.push(this.spacing + "<" + type + "> " + word + " </" + type + ">")
  }

  increaseSpacing() {
    this.spacing += "\t"
  }

  decreaseSpacing() {
    this.spacing = this.spacing.substring(1)
  }

  checkSymbol(s) {
    if (s === "<") { s = "&lt;" }
    else if (s === ">") { s = "&gt;" }
    else if (s === "&") { s = "&amp;" }

    if (this.tokenizer.symbol() === s) {
      this.writeTag(s, "symbol")
      return true
    } else {
      return false
    }
  }

  checkIdentifier() {
    if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
      return true
    } else {
      return false
    }
  }

  checkKeyword(k) {
    if (this.tokenizer.tokenType() === "KEYWORD" && this.tokenizer.token() === k) {
      this.writeTag(k, "keyword")
      return true
    } else {
      return false
    }
  }

  checkAndWriteType() {
    if (["INT", "CHAR", "BOOLEAN"].includes(this.tokenizer.keyword())) {
      this.writeTag(this.tokenizer.token(), "keyword")
      return true
    } else if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
      return true
    } else {
      return false
    }
  }

  compileClass() {
    this.tokenizer.advance()
    if (this.tokenizer.keyword() === "CLASS") {
      // write class
      this.output.push(this.spacing + "<class>")
      this.increaseSpacing()

      this.writeTag(this.tokenizer.token(), "keyword")

      // write class name
      this.tokenizer.advance()
      if (this.tokenizer.tokenType() === "IDENTIFIER") {
        this.writeTag(this.tokenizer.identifier(), "identifier")
      } else {
        console.warn("illegal class name identifier")
        return
      }

      // write {
      this.tokenizer.advance()
      if (!this.checkSymbol("{")) {
        console.warn("no openning { for class")
        return
      }

      // parse potential classVarDec
      this.tokenizer.advance()
      while (["STATIC", "FIELD"].includes(this.tokenizer.keyword())) {
        this.compileClassVarDec()
        this.tokenizer.advance()
      }

      // parse potential subroutineDec
      while (["CONSTRUCTOR", "FUNCTION", "METHOD"].includes(this.tokenizer.keyword())) {
        this.compileSubroutine()
        this.tokenizer.advance()
      }

      // write }
      if (!this.checkSymbol("}")) {
        console.warn("no closing } for class")
        return
      }

      if (this.tokenizer.hasMoreTokens()) {
        console.warn("addtional tokens after closing }")
      }

      // write close tag of class
      this.decreaseSpacing()
      this.output.push(this.spacing + "</class>")
    } else {
      console.warn("does not start with class")
      return
    }
  }

  compileClassVarDec() {
    this.output.push(this.spacing + "<classVarDec>")
    this.increaseSpacing()

    this.writeTag(this.tokenizer.token(), "keyword")

    // match type
    this.tokenizer.advance()
    if (!this.checkAndWriteType()) {
      console.warn("illegal type for class var dec")
      return
    }

    // match varName
    this.tokenizer.advance()
    if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
    } else {
      console.warn("illegal classVar identifier")
      return
    }

    // match potential ", varName" part
    this.tokenizer.advance()
    while (this.tokenizer.symbol() === ",") {
      this.writeTag(",", "symbol")
      this.tokenizer.advance()
      if (this.tokenizer.tokenType() === "IDENTIFIER") {
        this.writeTag(this.tokenizer.identifier(), "identifier")
      } else {
        console.warn("illegal classVar identifier")
        return
      }
      this.tokenizer.advance()
    }

    // match ;
    if (this.tokenizer.symbol() === ";") {
      this.writeTag(";", "symbol")
    } else {
      console.warn("no ending ;")
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</classVarDec>")
  }

  compileSubroutine() {
    // write subroutineDec tag
    this.output.push(this.spacing + "<subroutineDec>")

    // New level
    this.increaseSpacing()

    // already know that the current token start with constructor, function or method
    this.writeTag(this.tokenizer.token(), "keyword")

    // match return type
    this.tokenizer.advance()
    if (this.tokenizer.tokenType() === "KEYWORD" && this.tokenizer.token() === "void") {
      this.writeTag("void", "keyword")
    } else if (!this.checkAndWriteType()) {
      console.warn("Illegal type name for subroutine")
      return
    }

    // match subroutine identifier
    this.tokenizer.advance()
    if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
    } else {
      console.warn("illegal subroutine name")
      return
    }

    // match parameter list
    this.tokenizer.advance()
    if (this.tokenizer.symbol() === "(") {
      this.writeTag("(", "symbol")
      this.compileParameterList()
    } else {
      console.warn("no () after function name")
      return
    }

    // match the closing ) for the paramater list
    if (this.tokenizer.symbol() === ")") {
      this.writeTag(")", "symbol")
    } else {
      console.warn("no () after function name")
      return
    }

    // match subroutine body
    this.tokenizer.advance()
    if (this.tokenizer.symbol() === "{") {
      this.compileSubroutineBody()
    } else {
      console.warn("no { after function parameters")
      return
    }

    // decrease this.spacing
    this.decreaseSpacing()

    // write close subrountine tag
    this.output.push(this.spacing + "</subroutineDec>")
  }

  compileSubroutineBody() {
    this.output.push(this.spacing + "<subroutineBody>")
    this.increaseSpacing()

    this.writeTag("{", "symbol")

    this.tokenizer.advance()
    while (this.tokenizer.tokenType() === "KEYWORD" && this.tokenizer.token() === "var") {
      this.compileVarDec()
      this.tokenizer.advance()
    }

    this.compileStatements()

    // match }
    if (!this.checkSymbol("}")) {
      console.warn("no } found to close subroutine call")
      console.warn("current token is: " + this.tokenizer.token())
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</subroutineBody>")
  }

  compileParameterList() {
    this.output.push(this.spacing + "<parameterList>")
    this.increaseSpacing()

    // write type
    this.tokenizer.advance()
    if (this.checkAndWriteType()) {
      // match varName
      this.tokenizer.advance()
      if (this.tokenizer.tokenType() === "IDENTIFIER") {
        this.writeTag(this.tokenizer.identifier(), "identifier")
      } else {
        console.warn("illegal identifier in parameter list")
        return
      }

      // match other arguments
      this.tokenizer.advance()
      while (this.tokenizer.symbol() === ",") {
        this.writeTag(",", "symbol")
        this.tokenizer.advance()
        if (!this.checkAndWriteType()) {
          console.warn("illegal type name")
          return
        }
        this.tokenizer.advance()
        if (this.tokenizer.tokenType() === "IDENTIFIER") {
          this.writeTag(this.tokenizer.identifier(), "identifier")
        } else {
          console.warn("illegal identifier name")
          return
        }
        this.tokenizer.advance()
      }
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</parameterList>")
  }

  compileVarDec() {
    this.output.push(this.spacing + "<varDec>")
    this.increaseSpacing()

    // write var
    this.writeTag("var", "keyword")

    // check type
    this.tokenizer.advance()
    if (!this.checkAndWriteType()) {
      console.warn("illegal type for var")
      return
    }

    // check varName
    this.tokenizer.advance()
    if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
    } else {
      console.warn("illegal identifier for var")
      return
    }

    this.tokenizer.advance()
    while (this.tokenizer.symbol() === ",") {
      this.writeTag(",", "symbol")

      this.tokenizer.advance()
      if (this.tokenizer.tokenType() === "IDENTIFIER") {
        this.writeTag(this.tokenizer.identifier(), "identifier")
      } else {
        console.warn("illegal identifier for var")
        return
      }

      this.tokenizer.advance()
    }

    if (this.tokenizer.symbol() === ";") {
      this.writeTag(";", "symbol")
    } else {
      console.warn("varDec doesn't end with ;")
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</varDec>")
  }

  compileStatements() {
    this.output.push(this.spacing + "<statements>")
    this.increaseSpacing()

    while (this.tokenizer.tokenType() === "KEYWORD") {
      const keywordType = this.tokenizer.keyword()
      // compileIf needs to do one token look ahead to check "else",
      // so no more advance here.
      switch (keywordType) {
        case "LET":
          this.compileLet()
          this.tokenizer.advance()
          break
        case "IF":
          this.compileIf()
          break
        case "WHILE":
          this.compileWhile()
          this.tokenizer.advance()
          break
        case "DO":
          this.compileDo()
          this.tokenizer.advance()
          break
        case "RETURN":
          this.compileReturn()
          this.tokenizer.advance()
          break
        default:
          console.warn("illegal statement")
          return
      }
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</statements>")
  }

  compileDo() {
    this.output.push(this.spacing + "<doStatement>")
    this.increaseSpacing()

    this.writeTag("do", "keyword")

    this.tokenizer.advance()
    // Before call compileSubRoutineCall, first check if the current
    // token is valid identifier. Then advance again and check if the it is . or (
    if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")

      this.tokenizer.advance()
      if (this.checkSymbol(".") || this.checkSymbol("(")) {
        this.compileSubRoutineCall()
      } else {
        console.warn("Not valid subroutine call")
        return
      }
    } else {
      console.warn("Not a valid identifier for do statement")
      return
    }

    this.tokenizer.advance()
    if (!this.checkSymbol(";")) {
      console.warn("No closing ;")
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</doStatement>")
  }

  compileLet() {
    this.output.push(this.spacing + "<letStatement>")
    this.increaseSpacing()

    this.writeTag("let", "keyword")

    this.tokenizer.advance()
    if (!this.checkIdentifier()) {
      console.warn("Illegal identifier")
      return
    }

    this.tokenizer.advance()
    if (this.checkSymbol("[")) {
      this.tokenizer.advance()
      this.compileExpression()

      if (!this.checkSymbol("]")) {
        console.warn("No closing ], current: " + this.tokenizer.token())
        return
      }
      // if has [], advance and next should be =
      this.tokenizer.advance()
    }

    if (!this.checkSymbol("=")) {
      console.warn("No = found")
      return
    }

    this.tokenizer.advance()
    this.compileExpression()

    // No need to advance because compileExpression does one token look ahead
    if (!this.checkSymbol(";")) {
      console.warn("No ; found at the end of statement")
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</letStatement>")
  }

  compileWhile() {
    this.output.push(this.spacing + "<whileStatement>")
    this.increaseSpacing()

    this.writeTag("while", "keyword")

    this.tokenizer.advance()
    if (!this.checkSymbol("(")) {
      console.warn("No ( in while statement")
      return
    }

    this.tokenizer.advance()
    this.compileExpression()

    if (!this.checkSymbol(")")) {
      console.warn("No ) in while statement")
      return
    }

    this.tokenizer.advance()
    if (!this.checkSymbol("{")) {
      console.warn("No { in while statement")
      return
    }

    this.tokenizer.advance()
    this.compileStatements()

    if (!this.checkSymbol("}")) {
      console.warn("No } in while statement")
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</whileStatement>")
  }

  compileReturn() {
    this.output.push(this.spacing + "<returnStatement>")
    this.increaseSpacing()

    this.writeTag("return", "keyword")

    this.tokenizer.advance()
    // if the following is not ; then try to parse argument
    if (!this.checkSymbol(";")) {
      this.compileExpression()

      // after the expresison, it should end with ;
      if (!this.checkSymbol(";")) {
        console.warn("return statement not ending with ;")
      }
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</returnStatement>")
  }

  compileIf() {
    this.output.push(this.spacing + "<ifStatement>")
    this.increaseSpacing()

    this.writeTag("if", "keyword")

    this.tokenizer.advance()
    if (!this.checkSymbol("(")) {
      console.warn("No openning ( for if statement")
      return
    }

    this.tokenizer.advance()
    this.compileExpression()

    if (!this.checkSymbol(")")) {
      console.warn("No closing ) for if statement")
      return
    }

    this.tokenizer.advance()
    if (!this.checkSymbol("{")) {
      console.warn("No { for if statement")
      return
    }

    this.tokenizer.advance()
    this.compileStatements()

    if (!this.checkSymbol("}")) {
      console.warn("No } for if statement")
      console.warn("the current symbol is " + tokenizer.token())
      return
    }

    this.tokenizer.advance()
    if (this.checkKeyword("else")) {
      this.tokenizer.advance()
      if (!this.checkSymbol("{")) {
        console.warn("No { for else statment")
        return
      }

      this.tokenizer.advance()
      this.compileStatements()

      if (!this.checkSymbol("}")) {
        console.warn("No } for if statement")
        return
      }
      this.tokenizer.advance()
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</ifStatement>")
  }

  compileExpression() {
    this.output.push(this.spacing + "<expression>")
    this.increaseSpacing()

    this.compileTerm()

    while (this.checkSymbol("+") || this.checkSymbol("-") || this.checkSymbol("*") || this.checkSymbol("/") || this.checkSymbol("&") || this.checkSymbol("|") || this.checkSymbol("<") || this.checkSymbol(">") || this.checkSymbol("=")) {
      this.tokenizer.advance()
      this.compileTerm()
      // no advance here, because compileTerm needs to do one token look ahead
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</expression>")
  }

  compileTerm() {
    this.output.push(this.spacing + "<term>")
    this.increaseSpacing()

    if (this.tokenizer.tokenType() === "INT_CONST") {
      this.writeTag(this.tokenizer.intVal(), "integerConstant")
      this.tokenizer.advance()
    } else if (this.tokenizer.tokenType() === "STRING_CONST") {
      this.writeTag(this.tokenizer.stringVal(), "stringConstant")
      this.tokenizer.advance()
    } else if (this.checkKeyword("true") || this.checkKeyword("false") || this.checkKeyword("null") || this.checkKeyword("this")) {
      this.tokenizer.advance()
    } else if (this.checkSymbol("-") || this.checkSymbol("~")) {
      this.tokenizer.advance()
      this.compileTerm()
    } else if (this.tokenizer.tokenType() === "IDENTIFIER") {
      this.writeTag(this.tokenizer.identifier(), "identifier")
      this.tokenizer.advance()
      if (this.checkSymbol("[")) {
        this.compileArrayTerm()
        this.tokenizer.advance()
      } else if (this.checkSymbol("(") || this.checkSymbol(".")) {
        this.compileSubRoutineCall()
        this.tokenizer.advance()
      }
      // if doesn't match [, (, or ., it is a normal identifier
    } else if (this.tokenizer.tokenType() === "SYMBOL") {
      if (this.checkSymbol("(")) {
        this.tokenizer.advance()
        this.compileExpression()
        if (this.checkSymbol(")")) {
          this.tokenizer.advance()
        } else {
          console.warn("no closing bracket for term")
        }
      }
    } else {
      console.warn("illegal varName: " + this.tokenizer.token())
      return
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</term>")
  }

  compileArrayTerm() {
    this.tokenizer.advance()
    this.compileExpression()

    if (!this.checkSymbol("]")) {
      console.warn("No closing ] for the array expression")
    }
  }

  compileSubRoutineCall() {
    if (this.tokenizer.symbol() === "(") {
      this.tokenizer.advance()
      this.compileExpressionList()

      if (!this.checkSymbol(")")) {
        console.warn("No closing ) for the expressionlist")
        return
      }
    } else {
      this.tokenizer.advance()
      if (this.tokenizer.tokenType() === "IDENTIFIER") {
        this.writeTag(this.tokenizer.identifier(), "identifier")
      } else {
        console.warn("illegal identifier for subroutine call")
        return
      }

      this.tokenizer.advance()
      if (!this.checkSymbol("(")) {
        console.warn("Expecting a open bracket in subroutine call")
        return
      }

      this.tokenizer.advance()
      this.compileExpressionList()

      if (!this.checkSymbol(")")) {
        console.warn("No closing ) for the expressionlist")
        return
      }
    }
  }

  compileExpressionList() {
    this.output.push(this.spacing + "<expressionList>")
    this.increaseSpacing()

    if (this.tokenizer.symbol() !== ")") {
      this.compileExpression()

      // because compileExpression did 1 token look ahead, no advance here
      while (this.checkSymbol(",")) {
        this.tokenizer.advance()
        this.compileExpression()
      }
    }

    this.decreaseSpacing()
    this.output.push(this.spacing + "</expressionList>")
  }
}
