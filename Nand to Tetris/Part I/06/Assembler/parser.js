const fs = require("fs")

module.exports = class Parser {
  constructor(inputFile) {
    const commands = fs.readFileSync(inputFile, "utf-8")
    this.commands = commands.split("\n")
    this.currentCommand = null
    this.currentCommandType = null
  }

  hasMoreCommands() {
    return this.commands.length > 0
  }

  advance() {
    const command = this.commands.shift()
    this.currentCommand = command.replace(/\s|(\/\/).+/g, "")
  }

  commandType() {
    if (!this.currentCommand) {
      this.currentCommandType = null
    } else if (this.currentCommand.startsWith("@")) {
      this.currentCommandType = "A"
    } else if (this.currentCommand.startsWith("(")) {
      this.currentCommandType = "L"
    } else {
      this.currentCommandType = "C"
    }
    return this.currentCommandType
  }

  symbol() {
    if (this.currentCommandType === "A") {
      return this.currentCommand.slice(1)
    } else if (this.currentCommandType === "L") {
      return this.currentCommand.replace(/^\((.+)\)$/, "$1")
    }
  }

  comp() {
    if (this.currentCommandType === "C") {
      if (this.currentCommand.includes("=")) {
        return this.currentCommand.split("=")[1]
      } else if (this.currentCommand.includes(";")) {
        return this.currentCommand.split(";")[0]
      }
    }
  }

  dest() {
    if (this.currentCommandType === "C" && this.currentCommand.includes("=")) {
      return this.currentCommand.split("=")[0]
    }
  }

  jump() {
    if (this.currentCommandType === "C" && this.currentCommand.includes(";")) {
      return this.currentCommand.split(";")[1]
    }
  }
}
