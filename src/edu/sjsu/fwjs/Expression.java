package edu.sjsu.fwjs;


import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * FWJS expressions.
 */
public interface Expression {
    /**
     * Evaluate the expression in the context of the specified environment.
     */
    public Value evaluate(Environment env);
}

// NOTE: Using package access so that all implementations of Expression
// can be included in the same file.

/**
 * FWJS constants.
 */
class ValueExpr implements Expression {
    private Value val;
    public ValueExpr(Value v) {
        this.val = v;
    }
    public Value evaluate(Environment env) {
        return this.val;
    }
}

/**
 * Expressions that are a FWJS variable.
 */
class VarExpr implements Expression {
    private String varName;
    public VarExpr(String varName) {
        this.varName = varName;
    }
    public Value evaluate(Environment env) {
        return env.resolveVar(varName);
    }
}

/**
 * A print expression.
 */
class PrintExpr implements Expression {
    private Expression exp;
    public PrintExpr(Expression exp) {
        this.exp = exp;
    }
    public Value evaluate(Environment env) {
        Value v = exp.evaluate(env);
        System.out.println(v.toString());
        return v;
    }
}
/**
 * Binary operators (+, -, *, etc).
 * Currently only numbers are supported.
 */
class BinOpExpr implements Expression {
    private Op op;
    private Expression e1;
    private Expression e2;

    public BinOpExpr(Op op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @SuppressWarnings("incomplete-switch")
    public Value evaluate(Environment env) {
        // YOUR CODE HERE

        Value first = this.e1.evaluate(env);
        Value second = this.e2.evaluate(env);

        IntVal firstInt = (IntVal) first;
        IntVal secondInt = (IntVal) second;

        // ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, GT, GE, LT, LE, EQ
        switch (this.op) {
            case ADD:
                return new IntVal(firstInt.toInt() + secondInt.toInt());

            case SUBTRACT:
                return new IntVal(firstInt.toInt() - secondInt.toInt());

            case MULTIPLY:
                return new IntVal(firstInt.toInt() * secondInt.toInt());

            case DIVIDE:
                if (secondInt.toInt() == 0) {
                    throw new RuntimeException("Cannot divide by zero");
                }
                return new IntVal(firstInt.toInt() / secondInt.toInt());

            case MOD:
                return new IntVal(firstInt.toInt() % secondInt.toInt());

            case GT:
                if (firstInt.toInt() > secondInt.toInt()) {
                    return new BoolVal(true);
                }
                return new BoolVal(false);

            case GE:
                if (firstInt.toInt() >= secondInt.toInt()) {
                    return new BoolVal(true);
                }
                return new BoolVal(false);

            case LT:
                if (firstInt.toInt() < secondInt.toInt()) {
                    return new BoolVal(true);
                }
                return new BoolVal(false);

            case LE:
                if (firstInt.toInt() <= secondInt.toInt()) {
                    return new BoolVal(true);
                }
                return new BoolVal(false);

            case EQ:
                if (firstInt.toInt() == secondInt.toInt()) {
                    return new BoolVal(true);
                }
                return new BoolVal(false);

        }

        throw new RuntimeException("Not supported");
    }
}

/**
 * If-then-else expressions.
 * Unlike JS, if expressions return a value.
 */
class IfExpr implements Expression {
    private Expression cond;
    private Expression thn;
    private Expression els;

    public IfExpr(Expression cond, Expression thn, Expression els) {
        this.cond = cond;
        this.thn = thn;
        this.els = els;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE

        Value cond1 = this.cond.evaluate(env);
        //Value thn1 = this.thn.evaluate(env);
        //Value els1 = this.els.evaluate(env);

        // if cond, evaluate thn, else evaluate els
        if (((IntVal) cond1).toInt() != 0) {
            return this.thn.evaluate(env);
        } else {
            return this.els.evaluate(env);
        }

    }
}

/**
 * While statements (treated as expressions in FWJS, unlike JS).
 */
class WhileExpr implements Expression {
    private Expression cond;
    private Expression body;

    public WhileExpr(Expression cond, Expression body) {
        this.cond = cond;
        this.body = body;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        //Value cond1 = this.cond.evaluate(env);
        //Value body1 = this.body.evaluate(env);
        Value val = new IntVal(0); // this val keeps track of while loop value

        // while cond, evaluate body
        while (((BoolVal) this.cond.evaluate(env)).toBoolean()) {
            val = this.body.evaluate(env);
        }
        return val;
    }
}

/**
 * Sequence expressions (i.e. 2 back-to-back expressions).
 */
class SeqExpr implements Expression {
    private Expression e1;
    private Expression e2;

    public SeqExpr(Expression e1, Expression e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        return null;
    }
}

/**
 * Declaring a variable in the local scope.
 */
class VarDeclExpr implements Expression {
    private String varName;
    private Expression exp;

    public VarDeclExpr(String varName, Expression exp) {
        this.varName = varName;
        this.exp = exp;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        return null;
    }
}

/**
 * Updating an existing variable.
 * If the variable is not set already, it is added
 * to the global scope.
 */
class AssignExpr implements Expression {
    private String varName;
    private Expression e;

    public AssignExpr(String varName, Expression e) {
        this.varName = varName;
        this.e = e;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        return null;
    }
}

/**
 * A function declaration, which evaluates to a closure.
 */

    // FunctionDeclExpr takes a list of parameter names and a body.
    // When evaluated, it returns a closure included the environment
    // used when it was defined.
class FunctionDeclExpr implements Expression {
    private List<String> params;
    private Expression body;

    public FunctionDeclExpr(List<String> params, Expression body) {
        this.params = params;
        this.body = body;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        return new ClosureVal(params, body, env);
    }
}

/**
 * Function application.
 */
    // FunctionAppExpr takes an expression (which should evaluate to a closure)
    // and a list of arguments.  When evaluated, the environment is ignored.
    // Instead a new environment is created including all of the parameters with
    // the arguments bound to the parameter names.  Note that you will need to
    // evaluate each argument to a value before the closure.apply method can be used.
class FunctionAppExpr implements Expression {
    private Expression f;
    private List<Expression> args;

    public FunctionAppExpr(Expression f, List<Expression> args) {
        this.f = f;
        this.args = args;
    }

    public Value evaluate(Environment env) {
        // YOUR CODE HERE
        List<Value> argVals = args.stream().map (expr -> expr.evaluate(env)).toList();
        return ((ClosureVal)(f.evaluate(env))).apply (argVals);
    }
}

