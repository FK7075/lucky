package com.lucky.aop.aspectj;

import com.lucky.utils.annotation.Nullable;
import org.aspectj.weaver.patterns.NamePattern;
import org.aspectj.weaver.tools.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/5 0005 18:15
 */
public class AspectJExpressionPointcut {

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

//
//    /**
//     * Initialize the underlying AspectJ pointcut parser.
//     */
//    private PointcutParser initializePointcutParser(@Nullable ClassLoader classLoader) {
//        PointcutParser parser = PointcutParser
//                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
//                        SUPPORTED_PRIMITIVES, classLoader);
//        parser.registerPointcutDesignatorHandler(new BeanPointcutDesignatorHandler());
//        return parser;
//    }
//
//    /**
//     * Handler for the Spring-specific {@code bean()} pointcut designator
//     * extension to AspectJ.
//     * <p>This handler must be added to each pointcut object that needs to
//     * handle the {@code bean()} PCD. Matching context is obtained
//     * automatically by examining a thread local variable and therefore a matching
//     * context need not be set on the pointcut.
//     */
//    private class BeanPointcutDesignatorHandler implements PointcutDesignatorHandler {
//
//        private static final String BEAN_DESIGNATOR_NAME = "bean";
//
//        @Override
//        public String getDesignatorName() {
//            return BEAN_DESIGNATOR_NAME;
//        }
////
////        @Override
////        public ContextBasedMatcher parse(String expression) {
////            return new BeanContextMatcher(expression);
////        }
//    }

//    /**
//     * Matcher class for the BeanNamePointcutDesignatorHandler.
//     * <p>Dynamic match tests for this matcher always return true,
//     * since the matching decision is made at the proxy creation time.
//     * For static match tests, this matcher abstains to allow the overall
//     * pointcut to match even when negation is used with the bean() pointcut.
//     */
//    private class BeanContextMatcher implements ContextBasedMatcher {
//
//        private final NamePattern expressionPattern;
//
//        public BeanContextMatcher(String expression) {
//            this.expressionPattern = new NamePattern(expression);
//        }
//
//        @Override
//        @SuppressWarnings("rawtypes")
//        @Deprecated
//        public boolean couldMatchJoinPointsInType(Class someClass) {
//            return (contextMatch(someClass) == FuzzyBoolean.YES);
//        }
//
//        @Override
//        @SuppressWarnings("rawtypes")
//        @Deprecated
//        public boolean couldMatchJoinPointsInType(Class someClass, MatchingContext context) {
//            return (contextMatch(someClass) == FuzzyBoolean.YES);
//        }
//
//        @Override
//        public boolean matchesDynamically(MatchingContext context) {
//            return true;
//        }
//
//        @Override
//        public FuzzyBoolean matchesStatically(MatchingContext context) {
//            return contextMatch(null);
//        }
//
//        @Override
//        public boolean mayNeedDynamicTest() {
//            return false;
//        }
//
//        private FuzzyBoolean contextMatch(@Nullable Class<?> targetType) {
//            String advisedBeanName = getCurrentProxiedBeanName();
//            if (advisedBeanName == null) {  // no proxy creation in progress
//                // abstain; can't return YES, since that will make pointcut with negation fail
//                return FuzzyBoolean.MAYBE;
//            }
//            if (BeanFactoryUtils.isGeneratedBeanName(advisedBeanName)) {
//                return FuzzyBoolean.NO;
//            }
//            if (targetType != null) {
//                boolean isFactory = FactoryBean.class.isAssignableFrom(targetType);
//                return FuzzyBoolean.fromBoolean(
//                        matchesBean(isFactory ? BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName : advisedBeanName));
//            }
//            else {
//                return FuzzyBoolean.fromBoolean(matchesBean(advisedBeanName) ||
//                        matchesBean(BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName));
//            }
//        }
//
//        private boolean matchesBean(String advisedBeanName) {
//            return BeanFactoryAnnotationUtils.isQualifierMatch(
//                    this.expressionPattern::matches, advisedBeanName, beanFactory);
//        }
//    }
}
