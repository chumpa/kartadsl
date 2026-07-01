// Generated from D:/workspace/chumpa/kartadsl/src/test/resources/Grammar1.g4 by ANTLR 4.13.2
package scratch.grammar1;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Grammar1Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Grammar1Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#route}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoute(Grammar1Parser.RouteContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#endpoint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndpoint(Grammar1Parser.EndpointContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#processor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessor(Grammar1Parser.ProcessorContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#choice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChoice(Grammar1Parser.ChoiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#whenClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenClause(Grammar1Parser.WhenClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#otherwiseClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOtherwiseClause(Grammar1Parser.OtherwiseClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#filter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilter(Grammar1Parser.FilterContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#to}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTo(Grammar1Parser.ToContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#log}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLog(Grammar1Parser.LogContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#setBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetBody(Grammar1Parser.SetBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#delay}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelay(Grammar1Parser.DelayContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#transform}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransform(Grammar1Parser.TransformContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#beanExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeanExpression(Grammar1Parser.BeanExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#simpleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpression(Grammar1Parser.SimpleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#simpleBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleBody(Grammar1Parser.SimpleBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(Grammar1Parser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link Grammar1Parser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(Grammar1Parser.NumberContext ctx);
}