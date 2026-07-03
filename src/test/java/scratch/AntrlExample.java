package scratch;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import scratch.grammar1.Grammar1Lexer;
import scratch.grammar1.Grammar1Parser;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AntrlExample {
    @Test
    void grammar1mini() throws Exception {
        System.out.println("grammar1mini");

        CharStream charStream = CharStreams.fromStream(
                Objects.requireNonNull(getClass().getResourceAsStream("/g1mini.dsl")));
        Grammar1Lexer lexer = new Grammar1Lexer(charStream);
        Grammar1Parser parser = new Grammar1Parser(new CommonTokenStream(lexer));
        Grammar1Parser.ProgramContext program = parser.program();
        TreeViewer viewer = new TreeViewer(
                Arrays.asList(parser.getRuleNames()),
                program
        );
        viewer.open();

    }
}