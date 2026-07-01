// Generated from D:/workspace/chumpa/kartadsl/src/test/resources/Grammar1.g4 by ANTLR 4.13.2
package scratch.grammar1;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Grammar1Parser}.
 */
public interface Grammar1Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#route}.
	 * @param ctx the parse tree
	 */
	void enterRoute(Grammar1Parser.RouteContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#route}.
	 * @param ctx the parse tree
	 */
	void exitRoute(Grammar1Parser.RouteContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#endpoint}.
	 * @param ctx the parse tree
	 */
	void enterEndpoint(Grammar1Parser.EndpointContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#endpoint}.
	 * @param ctx the parse tree
	 */
	void exitEndpoint(Grammar1Parser.EndpointContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#processor}.
	 * @param ctx the parse tree
	 */
	void enterProcessor(Grammar1Parser.ProcessorContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#processor}.
	 * @param ctx the parse tree
	 */
	void exitProcessor(Grammar1Parser.ProcessorContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#choice}.
	 * @param ctx the parse tree
	 */
	void enterChoice(Grammar1Parser.ChoiceContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#choice}.
	 * @param ctx the parse tree
	 */
	void exitChoice(Grammar1Parser.ChoiceContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#whenClause}.
	 * @param ctx the parse tree
	 */
	void enterWhenClause(Grammar1Parser.WhenClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#whenClause}.
	 * @param ctx the parse tree
	 */
	void exitWhenClause(Grammar1Parser.WhenClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#otherwiseClause}.
	 * @param ctx the parse tree
	 */
	void enterOtherwiseClause(Grammar1Parser.OtherwiseClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#otherwiseClause}.
	 * @param ctx the parse tree
	 */
	void exitOtherwiseClause(Grammar1Parser.OtherwiseClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#filter}.
	 * @param ctx the parse tree
	 */
	void enterFilter(Grammar1Parser.FilterContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#filter}.
	 * @param ctx the parse tree
	 */
	void exitFilter(Grammar1Parser.FilterContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#to}.
	 * @param ctx the parse tree
	 */
	void enterTo(Grammar1Parser.ToContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#to}.
	 * @param ctx the parse tree
	 */
	void exitTo(Grammar1Parser.ToContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#log}.
	 * @param ctx the parse tree
	 */
	void enterLog(Grammar1Parser.LogContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#log}.
	 * @param ctx the parse tree
	 */
	void exitLog(Grammar1Parser.LogContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#setBody}.
	 * @param ctx the parse tree
	 */
	void enterSetBody(Grammar1Parser.SetBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#setBody}.
	 * @param ctx the parse tree
	 */
	void exitSetBody(Grammar1Parser.SetBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#delay}.
	 * @param ctx the parse tree
	 */
	void enterDelay(Grammar1Parser.DelayContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#delay}.
	 * @param ctx the parse tree
	 */
	void exitDelay(Grammar1Parser.DelayContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#transform}.
	 * @param ctx the parse tree
	 */
	void enterTransform(Grammar1Parser.TransformContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#transform}.
	 * @param ctx the parse tree
	 */
	void exitTransform(Grammar1Parser.TransformContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#beanExpression}.
	 * @param ctx the parse tree
	 */
	void enterBeanExpression(Grammar1Parser.BeanExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#beanExpression}.
	 * @param ctx the parse tree
	 */
	void exitBeanExpression(Grammar1Parser.BeanExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(Grammar1Parser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(Grammar1Parser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#simpleBody}.
	 * @param ctx the parse tree
	 */
	void enterSimpleBody(Grammar1Parser.SimpleBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#simpleBody}.
	 * @param ctx the parse tree
	 */
	void exitSimpleBody(Grammar1Parser.SimpleBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(Grammar1Parser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(Grammar1Parser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link Grammar1Parser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(Grammar1Parser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link Grammar1Parser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(Grammar1Parser.NumberContext ctx);
}