package lox;

class Interpreter implements Expr.Visitor<Object> {
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case TokenType.Minus -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left - (double) right;
            }
            case TokenType.Slash -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left / (double) right;
            }
            case TokenType.Star -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left * (double) right;
            }
            case TokenType.Plus -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double) left + (double) right;
                } else if (left instanceof String && right instanceof String) {
                    yield (String) left + (String) right;
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
                }
            }
            case TokenType.Greater -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left > (double) right;
            }
            case TokenType.GreaterEqual -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left >= (double) right;
            }
            case TokenType.Less -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left < (double) right;
            }
            case TokenType.LessEqual -> {
                checkNumberOperand(expr.operator, left, right);
                yield (double) left <= (double) right;
            }
            case TokenType.BangEqual -> !isEqual(left, right);
            case TokenType.EqualEqual -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case TokenType.Bang -> !isTruthy(right);
            case TokenType.Minus -> {
                checkNumberOperand(expr.operator, right);
                yield - (double) right;
            }
            default -> null;
        };
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        throw new RuntimeError(operator, "Operand must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return true;
        }
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        } else if (a == null) {
            return false;
        } else {
            return a.equals(b);
        }
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        } else {
            return object.toString();
        }
    }
}
