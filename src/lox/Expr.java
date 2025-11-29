package lox;

import java.util.List;

abstract class Expr {
    interface Visitor<RetType> {
        RetType visitAssignExpr(Assign expr);
        RetType visitBinaryExpr(Binary expr);
        RetType visitGroupingExpr(Grouping expr);
        RetType visitLiteralExpr(Literal expr);
        RetType visitUnaryExpr(Unary expr);
        RetType visitVariableExpr(Variable expr);
    }

    static class Assign extends Expr {
        final Token name;
        final Expr value;

        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class Grouping extends Expr {
        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Unary extends Expr {
        final Token operator;
        final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    static class Variable extends Expr {
        final Token name;

        Variable(Token name) {
            this.name = name;
        }

        @Override
        <RetType> RetType accept(Visitor<RetType> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    abstract <RetType> RetType accept(Visitor<RetType> visitor);
}
