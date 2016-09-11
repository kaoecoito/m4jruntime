package br.com.sisprof.m4jruntime.compiler;

import br.com.sisprof.m4jruntime.lang.MUMPSParser;
import br.com.sisprof.m4jruntime.runtime.instructions.Block;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kaoe on 11/09/16.
 */
public class BlockIndent implements BlockStack {

    private final Block block;
    private final List<MUMPSParser.CmdContext> cmds = new LinkedList<>();

    public BlockIndent(Block block) {
        this.block = block;
    }


    public Block getBlock() {
        return block;
    }

    public List<MUMPSParser.CmdContext> getCmds() {
        return Collections.unmodifiableList(cmds);
    }


    public void add(MUMPSParser.CmdContext cmd) {
        cmds.add(cmd);
    }

}
