package lox;

import java.util.List;

abstract class Stmt {
    interface Visitor<RetType> {
        RetType visitBlockStmt(Block stmt);
        RetType visitClassStmt(Class stmt);
        RetType visitExpressionStmt(Expression stmt);
        RetType visitFunctionStmt(Function stmt);
        RetType visitIfStmt(If stmt);
        RetType visitPrintStmt(Print stmt);
        RetType visitReturnStmt(Return stmt);
        RetType visitVarStmt(Var stmt);
        RetType visitWhileStmt(While stmt);
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

    static class Class extends Stmt {
        final Token name;
        final Expr.Variable superclass;
        final List<Stmt.Function> methods;

        Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitClassStmt(this);
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

    static class Function extends Stmt {
        final Token name;
        final List<Token> params;
        final List<Stmt> body;

        Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitFunctionStmt(this);
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

    static class Return extends Stmt {
        final Token keyword;
        final Expr value;

        Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitReturnStmt(this);
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

    static class While extends Stmt {
        final Expr condition;
        final Stmt body;

        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    abstract <RetType> RetType accept(Visitor<RetType> visitor);
}
