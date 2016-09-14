package br.com.sisprof.m4jruntime.compiler;

import br.com.sisprof.m4jruntime.lang.MUMPSLexer;
import br.com.sisprof.m4jruntime.lang.MUMPSParser;
import br.com.sisprof.m4jruntime.lang.MUMPSParserVisitor;
import br.com.sisprof.m4jruntime.runtime.*;
import br.com.sisprof.m4jruntime.runtime.instructions.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by kaoe on 06/09/16.
 */
public class MumpsCompiler implements MUMPSParserVisitor<Object> {

    private final Routine routine;
    private final MUMPSParser parser;

    private final Deque<BlockStack> lineBlock = new LinkedList<>();

    private int currentIndent;
    private int espectedIndent;

    private final Map<String,Method> mumpsCommands = new HashMap<>();

    public MumpsCompiler(File file) throws IOException {
        this.routine = new Routine();
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(file));
        MUMPSLexer lexer = new MUMPSLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        this.parser = new MUMPSParser(tokens);
        this.scanMethods();
    }

    private void scanMethods() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method:methods) {
            if (method.isAnnotationPresent(MumpsCompiledCommand.class)) {
                MumpsCompiledCommand mumpsCompiledCommand = method.getAnnotation(MumpsCompiledCommand.class);
                String[] cmds = mumpsCompiledCommand.value();
                if (cmds!=null) {
                    for (String cmd:cmds) {
                        mumpsCommands.put(cmd.toUpperCase(), method);
                    }
                }
            }
        }
    }

    public Routine getRoutine() {
        return routine;
    }

    public boolean compile() {
        visitFile(parser.file());
        return true;
    }

    @Override
    public Object visitFile(MUMPSParser.FileContext ctx) {

        if (ctx.entryPoint()!=null) {
            visitEntryPoint(ctx.entryPoint());
        }

        for (MUMPSParser.LineContext line:ctx.line()) {
            visitLine(line);
        }

        return null;
    }

    @Override
    public Object visitLine(MUMPSParser.LineContext ctx) {

        if (ctx.entryPoint()!=null) {
            visitEntryPoint(ctx.entryPoint());
        }

        int indent = (ctx.DOT() == null) ? 0 : ctx.DOT().size();
        if (indent<espectedIndent) {
            espectedIndent = indent;
        }

        processCmds(indent, ctx.cmd());

        processBlockStack();

        return null;
    }

    private void processCmds(int indent, List<MUMPSParser.CmdContext> cmds) {
        if (cmds==null) return;
        this.currentIndent = indent;

        BlockIndent blockRundown;
        do {
            blockRundown = getBlockIndent();
            if (blockRundown != null && blockRundown.getBlock().getIndent() >= indent) {
                lineBlock.removeFirst();
                for (MUMPSParser.CmdContext cmd : blockRundown.getCmds()) {
                    visitCmd(cmd);
                }
                processBlockStack();
            } else {
                blockRundown = null;
            }
        } while (blockRundown!=null);

        for (MUMPSParser.CmdContext cmd:cmds) {
            BlockIndent blockIndent = getBlockIndent();
            if (blockIndent!=null && blockIndent.getBlock().getLine()==cmd.getStart().getLine()) {
                blockIndent.add(cmd);
            } else {
                visitCmd(cmd);
            }
        }
        this.currentIndent = 0;
    }

    private void processBlockStack() {
        while (lineBlock.size()>0) {
            BlockStack block = lineBlock.getFirst();
            if (block instanceof BlockIndent) {
                break;
            }
            if (block instanceof BlockJump) {
                lineBlock.removeFirst();
                ((BlockJump)block).getInstruction().setJump(routine.getStackSize()-1);
            } else if (block instanceof BlockFor) {
                lineBlock.removeFirst();
                BlockFor blockFor = (BlockFor)block;
                int line = blockFor.getSetup().getLine();
                int indent = blockFor.getSetup().getIndent();

                routine.add(ForIncrement.create(indent, line));
                routine.add(Jump.create(indent, line, blockFor.getGotoStack()));
                routine.add(ForEnd.create(indent, line));
            }
        }
    }

    private BlockIndent getBlockIndent() {
        BlockIndent blockIndent = null;
        if (!lineBlock.isEmpty()) {
            BlockStack stack = lineBlock.getFirst();
            if (stack instanceof BlockIndent) {
                blockIndent = (BlockIndent)stack;
            }
        }
        return blockIndent;
    }


    @Override
    public Object visitLines(MUMPSParser.LinesContext ctx) {
        for (MUMPSParser.LineContext line:ctx.line()) {
            visitLine(line);
        }
        return null;
    }

    @Override
    public Object visitEntryPoint(MUMPSParser.EntryPointContext ctx) {

        int line = ctx.getStart().getLine();
        int args = 0;
        String name = ctx.name.getText();

        if (ctx.entryPointArgs()!=null) {
            args = ctx.entryPointArgs().entryPointArg().size();
        }

        routine.add(new LabelInfo(name, routine.getStackSize(), args));
        routine.add(Label.create(line));

        if (ctx.entryPointArgs()!=null) {
            visitEntryPointArgs(ctx.entryPointArgs());
        }

        return null;
    }

    @Override
    public Object visitEntryPointArgs(MUMPSParser.EntryPointArgsContext ctx) {
        for (MUMPSParser.EntryPointArgContext arg:ctx.entryPointArg()) {
            visitEntryPointArg(arg);
        }
        return null;
    }

    @Override
    public Object visitEntryPointArg(MUMPSParser.EntryPointArgContext ctx) {

        int line = ctx.getStart().getLine();

        int argIndex = routine.add(new ConstantValueString(ctx.ID().getText()));

        routine.add(NewVariable.create(currentIndent, line, argIndex));

        StoreVariable storeVariable = StoreVariable.create(currentIndent, line, argIndex);

        if (ctx.literal()!=null) {
            visitLiteral(ctx.literal());
            routine.add(storeVariable);
        }
        routine.add(storeVariable);

        return null;
    }

    @Override
    public Object visitCmd(MUMPSParser.CmdContext ctx) {

        JumpIfFalse jumpPostCond = null;
        if (ctx.cmdPostCond()!=null) {
            jumpPostCond = visitCmdPostCond(ctx.cmdPostCond());
        }

        String name = ctx.ID().getText().toUpperCase();

        if (mumpsCommands.containsKey(name.toUpperCase())) {
            visitCmdAnnotation(ctx, name);
        } else {
            visitCmdUnknow(ctx, name);
        }

        if (jumpPostCond!=null) {
            jumpPostCond.setJump(routine.getStackSize()-1);
        }

        return null;
    }

    private void visitCmdAnnotation(MUMPSParser.CmdContext ctx, String name) {
        Method method = mumpsCommands.get(name.toUpperCase());
        // TODO Implementar logs e debug
        try {
            method.invoke(this, ctx);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void visitCmdUnknow(MUMPSParser.CmdContext ctx, String name) {
        int line = ctx.getStart().getLine();

        int args = ctx.expr().size();

        if (!ctx.expr().isEmpty()) {
            visitExpr(ctx.expr());
        } else if (ctx.args()!=null && !ctx.args().isEmpty()) {
            args = ctx.args().expr().size();
            visitArgs(ctx.args());
        }

        int nameIndex = routine.add(new ConstantValueString(name));

        routine.add(Constant.create(currentIndent, line, nameIndex));
        routine.add(Command.create(currentIndent, line, args));

    }

    @MumpsCompiledCommand({"IF","I"})
    private void visitCmdIf(MUMPSParser.CmdContext ctx) {
        if (!ctx.expr().isEmpty()) {
            int line = ctx.getStart().getLine();
            int size = ctx.expr().size();

            int testIndex = routine.add(new ConstantValueString("$TEST"));

            visitExpr(ctx.expr(0));

            if (size>1) {
                for (int i=1;i<size;i++) {
                    visitExpr(ctx.expr(i));
                    routine.add(BinaryOR.create(currentIndent, line));
                }
            }

            routine.add(Duplicate.create(currentIndent, line));
            routine.add(StoreVariable.create(currentIndent, line, testIndex));

            JumpIfFalse jump = JumpIfFalse.create(currentIndent, line);
            routine.add(jump);
            lineBlock.addFirst(new BlockJump(jump));
        } else {
            routine.add(NoOp.create());
        }
    }

    @MumpsCompiledCommand({"ELSE","E"})
    private void visitCmdElse(MUMPSParser.CmdContext ctx) {
        int line = ctx.getStart().getLine();
        int testIndex = routine.add(new ConstantValueString("$TEST"));

        routine.add(LoadVariable.create(currentIndent, line, testIndex));

        JumpIfTrue jump = JumpIfTrue.create(currentIndent, line);
        routine.add(jump);
        lineBlock.addFirst(new BlockJump(jump));
    }

    @MumpsCompiledCommand({"NEW","N"})
    private void visitCmdNew(MUMPSParser.CmdContext ctx) {
        if (!ctx.expr().isEmpty()) {
            int line = ctx.getStart().getLine();
            for (MUMPSParser.ExprContext exp:ctx.expr()) {
                String param = exp.getText();
                int paramIndex = routine.add(new ConstantValueString(param));
                routine.add(NewVariable.create(currentIndent, line, paramIndex));
            }
        } else {
            routine.add(NoOp.create());
        }
    }

    @MumpsCompiledCommand({"SET","S"})
    private void visitCmdSet(MUMPSParser.CmdContext ctx) {

        int line = ctx.getStart().getLine();

        // TODO Implementar erros no compilador

        for (MUMPSParser.ExprContext expr:ctx.expr()) {
            MUMPSParser.ExprBinaryContext item = (MUMPSParser.ExprBinaryContext) expr;

            MUMPSParser.ExprContext left = item.expr(0);
            MUMPSParser.ExprContext right = item.expr(1);

            List<MUMPSParser.ExprContext> vars = new ArrayList<>();
            if (left instanceof MUMPSParser.ExprFormatContext) {
                vars.add(left);
            } else if (left instanceof MUMPSParser.ExprListContext) {
                MUMPSParser.ExprListContext list = (MUMPSParser.ExprListContext)left;
                vars.addAll(list.expr());
            }

            visitExpr(right);
            for (MUMPSParser.ExprContext var:vars) {
                int varIndex = routine.add(new ConstantValueString(var.getText()));

                routine.add(Duplicate.create(currentIndent, line));

                routine.add(StoreVariable.create(currentIndent, line, varIndex));
            }
            routine.add(PopStack.create(currentIndent, line));

        }

    }

    @MumpsCompiledCommand({"DO","D"})
    private void visitCmdDo(MUMPSParser.CmdContext ctx) {

        int line = ctx.getStart().getLine();

        // TODO Implamentar outras opções do Do
        if (ctx.expr().isEmpty()) {
            Block block = Block.create(currentIndent, line);
            routine.add(block);
            lineBlock.addFirst(new BlockIndent(block));
            espectedIndent++;
        }

    }

    @MumpsCompiledCommand({"FOR","F"})
    private void visitCmdFor(MUMPSParser.CmdContext ctx) {
        int line = ctx.getStart().getLine();

        if (ctx.expr().isEmpty() && ctx.args().isEmpty()) {
            int stackPos = routine.getStackSize();
            ForSetup setup = ForSetup.create(currentIndent, line);
            routine.add(setup);
            routine.add(NoOp.create());
            lineBlock.addFirst(new BlockFor(setup, stackPos+1));
        } else if (ctx.expr().size()>=1 && ctx.expr(0) instanceof MUMPSParser.ExprBinaryContext){
            int items = ctx.expr().size();
            MUMPSParser.ExprBinaryContext item = (MUMPSParser.ExprBinaryContext)ctx.expr(0);

            MUMPSParser.ExprContext left = item.expr(0);
            MUMPSParser.ExprContext right = item.expr(1);

            visitExpr(right);
            for (int i=1;i<items;i++) {
                visitExpr(ctx.expr(i));
            }
            if (items>1) {
                routine.add(Rotate.create(currentIndent, line, items));
            }

            int varIdx = routine.add(new ConstantValueString(left.getText()));
            routine.add(Constant.create(currentIndent, line, varIdx));

            int stackPos = routine.getStackSize();
            ForSetup setup = ForSetup.create(currentIndent, line, items);
            routine.add(setup);
            routine.add(NoOp.create());
            lineBlock.addFirst(new BlockFor(setup, stackPos+1));
        }

    }

    @MumpsCompiledCommand({"QUIT","Q"})
    private void visitCmdQuit(MUMPSParser.CmdContext ctx) {

        int line = ctx.getStart().getLine();

        if (ctx.expr().isEmpty()) {
            routine.add(Return.create(currentIndent, line));
        } else {
            // TODO Implementar erro para mais parametros
            visitExpr(ctx.expr().get(0));
            routine.add(Return.create(currentIndent, line, 1));
        }

    }

    @Override
    public JumpIfFalse visitCmdPostCond(MUMPSParser.CmdPostCondContext ctx) {
        visitExpr(ctx.expr());
        JumpIfFalse jump = JumpIfFalse.create(currentIndent, ctx.getStart().getLine());
        routine.add(jump);
        return jump;
    }

    private Object visitExpr(List<MUMPSParser.ExprContext> context) {
        int line = -1;
        for (MUMPSParser.ExprContext expr:context) {
            if (line<0) {
                line = expr.getStart().getLine();
            }
            visitExpr(expr);
        }
        if (context.size()>1) {
            routine.add(Rotate.create(currentIndent, line, context.size()));
        }
        return null;
    }

    private Object visitExpr(MUMPSParser.ExprContext context) {

        if (context instanceof MUMPSParser.ExprLiteralContext) {
            visitExprLiteral((MUMPSParser.ExprLiteralContext)context);
        } else if (context instanceof MUMPSParser.ExprFormatContext) {
            visitExprFormat((MUMPSParser.ExprFormatContext)context);
        } else if (context instanceof MUMPSParser.ExprFuncContext) {
            visitExprFunc((MUMPSParser.ExprFuncContext)context);
        } else if (context instanceof MUMPSParser.ExprVarContext) {
            visitExprVar((MUMPSParser.ExprVarContext)context);
        } else if (context instanceof MUMPSParser.ExprRefContext) {
            visitExprRef((MUMPSParser.ExprRefContext)context);
        } else if (context instanceof MUMPSParser.ExprIndrExprContext) {
            visitExprIndrExpr((MUMPSParser.ExprIndrExprContext)context);
        } else if (context instanceof MUMPSParser.ExprIndrRefContext) {
            visitExprIndrRef((MUMPSParser.ExprIndrRefContext)context);
        } else if (context instanceof MUMPSParser.ExprIndrVarContext) {
            visitExprIndrVar((MUMPSParser.ExprIndrVarContext)context);
        } else if (context instanceof MUMPSParser.ExprIndrFuncContext) {
            visitExprIndrFunc((MUMPSParser.ExprIndrFuncContext)context);
        } else if (context instanceof MUMPSParser.ExprUnaryContext) {
            visitExprUnary((MUMPSParser.ExprUnaryContext)context);
        } else if (context instanceof MUMPSParser.ExprBinaryContext) {
            visitExprBinary((MUMPSParser.ExprBinaryContext)context);
        } else if (context instanceof MUMPSParser.ExprMatchContext) {
            visitExprMatch((MUMPSParser.ExprMatchContext)context);
        } else if (context instanceof MUMPSParser.ExprGroupContext) {
            visitExprGroup((MUMPSParser.ExprGroupContext)context);
        } else if (context instanceof MUMPSParser.ExprListContext) {
            visitExprList((MUMPSParser.ExprListContext)context);
        } else if (context instanceof MUMPSParser.ExprLineRefContext) {
            visitExprLineRef((MUMPSParser.ExprLineRefContext)context);
        } else if (context instanceof MUMPSParser.ExprColinContext) {
            visitExprColin((MUMPSParser.ExprColinContext)context);
        }

        return null;
    }

    @Override
    public Object visitExprVar(MUMPSParser.ExprVarContext ctx) {

        int line = ctx.getStart().getLine();
        String varName = ctx.getText();
        if (varName.startsWith("$")) {
            int funcIndex = routine.add(new ConstantValueString(varName));
            routine.add(Constant.create(currentIndent, line, funcIndex));
            routine.add(Command.create(currentIndent, line, 0));
        }

        return null;
    }

    @Override
    public Object visitExprLineRef(MUMPSParser.ExprLineRefContext ctx) {
        return null;
    }

    @Override
    public Object visitExprFormat(MUMPSParser.ExprFormatContext ctx) {
        visitFormat(ctx.format());
        return null;
    }

    @Override
    public Object visitExprIndrExpr(MUMPSParser.ExprIndrExprContext ctx) {
        return null;
    }

    @Override
    public Object visitExprIndrVar(MUMPSParser.ExprIndrVarContext ctx) {
        return null;
    }

    @Override
    public Object visitExprRef(MUMPSParser.ExprRefContext ctx) {
        return null;
    }

    @Override
    public Object visitExprIndrFunc(MUMPSParser.ExprIndrFuncContext ctx) {
        return null;
    }

    @Override
    public Object visitExprIndrRef(MUMPSParser.ExprIndrRefContext ctx) {
        return null;
    }

    @Override
    public Object visitExprBinary(MUMPSParser.ExprBinaryContext ctx) {

        int line = ctx.getStart().getLine();
        String oper = ctx.OPER().getText();

        MUMPSParser.ExprContext left = ctx.expr(0);

        if (left instanceof MUMPSParser.ExprFormatContext && !((MUMPSParser.ExprFormatContext)left).format().OPER().isEmpty()) {
            MUMPSParser.FormatContext format = ((MUMPSParser.ExprFormatContext)left).format();
            List<String> opers = new ArrayList<>();
            for (TerminalNode node:format.OPER()) {
                opers.add(node.getText());
            }
            visitExpr(ctx.expr(1));
            routine.add(OperatorFactory.createUnary(currentIndent, line, oper));

            Collections.reverse(opers);
            for (String item:opers) {
                routine.add(OperatorFactory.createUnary(currentIndent, line, item));
            }
        } else {
            visitExpr(left);
            visitExpr(ctx.expr(1));

            routine.add(Rotate.create(currentIndent, line, 2));
            routine.add(OperatorFactory.createBinary(currentIndent, line, oper));
        }

        return null;
    }

    @Override
    public Object visitExprGroup(MUMPSParser.ExprGroupContext ctx) {
        visitExpr(ctx.expr());
        return null;
    }

    @Override
    public Object visitExprList(MUMPSParser.ExprListContext ctx) {
        visitExpr(ctx.expr());
        return null;
    }

    @Override
    public Object visitExprUnary(MUMPSParser.ExprUnaryContext ctx) {

        int line = ctx.getStart().getLine();
        String oper = ctx.OPER().getText();

        visitExpr(ctx.expr());

        routine.add(OperatorFactory.createUnary(currentIndent, line, oper));

        return null;
    }

    @Override
    public Object visitExprColin(MUMPSParser.ExprColinContext ctx) {

        int line = ctx.getStart().getLine();

        List<MUMPSParser.ExprContext> expr = new ArrayList<>();
        resolvColinContext(ctx, expr);

        visitExpr(expr);

        routine.add(MultiVariable.create(currentIndent, line, expr.size()));

        return null;
    }

    private void resolvColinContext(MUMPSParser.ExprColinContext ctx, List<MUMPSParser.ExprContext> exprList) {
        for (MUMPSParser.ExprContext exp:ctx.expr()) {
            if (exp instanceof MUMPSParser.ExprColinContext) {
                resolvColinContext((MUMPSParser.ExprColinContext)exp, exprList);
            } else {
                exprList.add(exp);
            }
        }
    }

    @Override
    public Object visitExprLiteral(MUMPSParser.ExprLiteralContext ctx) {
        visitLiteral(ctx.literal());
        return null;
    }

    @Override
    public Object visitExprFunc(MUMPSParser.ExprFuncContext ctx) {
        visitFunc(ctx.func());
        return null;
    }

    @Override
    public Object visitExprMatch(MUMPSParser.ExprMatchContext ctx) {
        return null;
    }

    @Override
    public Object visitLiteral(MUMPSParser.LiteralContext ctx) {

        int line = ctx.getStart().getLine();

        ConstantValue val = null;
        if (ctx.STR_LITERAL()!=null) {
            String strVal = ctx.STR_LITERAL().getText();
            val = new ConstantValueString(strVal.substring(1, strVal.length()-1));
        } else if (ctx.NUM_LITERAL()!=null) {
            val = new ConstantValueNumber(NumberOperations.toNumber(ctx.NUM_LITERAL().getText()));
        }
        int constantIndex = routine.add(val);
        routine.add(Constant.create(currentIndent, line, constantIndex));

        return null;
    }

    @Override
    public Object visitFormat(MUMPSParser.FormatContext ctx) {
        int line = ctx.getStart().getLine();
        if (ctx.ID()!=null) {
            String varName = ctx.ID().getText();
            int varIndex = routine.add(new ConstantValueString(varName));
            routine.add(LoadVariable.create(currentIndent, line, varIndex));
        } else if (!ctx.OPER().isEmpty()) {
            int operatorIndex = routine.add(new ConstantValueOperator(ctx.OPER()));
            routine.add(Constant.create(currentIndent, line, operatorIndex));
        }
        return null;
    }

    @Override
    public Object visitLineRef(MUMPSParser.LineRefContext ctx) {
        return null;
    }

    @Override
    public Object visitFunc(MUMPSParser.FuncContext ctx) {
        String name = ctx.flags.getText()+ctx.name.getText();
        int line = ctx.getStart().getLine();

        int args = 0;
        if (ctx.args()!=null) {
            visitArgs(ctx.args());
            args = ctx.args().expr().size();
        }

        int nameIndex = routine.add(new ConstantValueString(name));

        routine.add(Constant.create(currentIndent, line, nameIndex));
        routine.add(Command.create(currentIndent, line, args));

        return null;
    }

    @Override
    public Object visitVar(MUMPSParser.VarContext ctx) {
        return null;
    }

    @Override
    public Object visitRef(MUMPSParser.RefContext ctx) {
        return null;
    }

    public Object visitArgs(List<MUMPSParser.ArgsContext> ctx) {
        for (MUMPSParser.ArgsContext args:ctx) {
            visitArgs(args);
        }
        return null;
    }

    @Override
    public Object visitArgs(MUMPSParser.ArgsContext ctx) {
        visitExpr(ctx.expr());
        return null;
    }

    @Override
    public Object visitNamespace(MUMPSParser.NamespaceContext ctx) {
        return null;
    }

    @Override
    public Object visitExprPatternItem(MUMPSParser.ExprPatternItemContext ctx) {
        return null;
    }

    @Override
    public Object visit(ParseTree parseTree) {
        return null;
    }

    @Override
    public Object visitChildren(RuleNode ruleNode) {
        return null;
    }

    @Override
    public Object visitTerminal(TerminalNode terminalNode) {
        return null;
    }

    @Override
    public Object visitErrorNode(ErrorNode errorNode) {
        return null;
    }


}
