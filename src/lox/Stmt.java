package lox;

import java.util.List;

abstract class Stmt {
    interface Visitor<RetType> {
        RetType visitBlockStmt(Block stmt);
        RetType visitExpressionStmt(Expression stmt);
        RetType visitIfStmt(If stmt);
        RetType visitPrintStmt(Print stmt);
        RetType visitVarStmt(Var stmt);
    }

    static class Block extends Stmt {
        final List<Stmt> statements;

        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class If extends Stmt {
        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    abstract <RetType> RetType accept(Visitor<RetType> visitor);
}
