package com.lucky.aop.aspectj;

import com.lucky.aop.core.AopChain;
import com.lucky.aop.core.TargetMethodSignature;
import com.lucky.utils.annotation.Nullable;
import com.lucky.utils.base.Assert;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 9:43
 */
public class AopChainProceedingJoinPoint implements ProceedingJoinPoint, JoinPoint.StaticPart {


    private final AopChain aopChain;

    private final TargetMethodSignature targetMethodSignature;

    private final Object targetObject;

    @Nullable
    private Object[] args;

    /** Lazily initialized signature object. */
    @Nullable
    private Signature signature;

    /** Lazily initialized source location object. */
    @Nullable
    private SourceLocation sourceLocation;


    /**
     * Create a new AopChainProceedingJoinPoint, wrapping the given
     * Lucky AopChain object and TargetMethodSignature object.
     * @param aopChain the Lucky AopChain
     */
    public AopChainProceedingJoinPoint(AopChain aopChain, TargetMethodSignature targetMethodSignature) {
        Assert.notNull(aopChain, "AopChain must not be null");
        this.aopChain = aopChain;
        Assert.notNull(aopChain, "TargetMethodSignature must not be null");
        this.targetMethodSignature=targetMethodSignature;
        this.targetObject= this.targetMethodSignature.getTargetObject();
    }


    @Override
    public void set$AroundClosure(AroundClosure aroundClosure) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Nullable
    public Object proceed() throws Throwable {
        return this.aopChain.proceed();
    }

    @Override
    @Nullable
    public Object proceed(Object[] arguments) throws Throwable {
        this.aopChain.setArgument(arguments);
        return this.aopChain.proceed();
    }

    /**
     * Returns the Lucky AOP proxy. Cannot be {@code null}.
     */
    @Override
    public Object getThis() {
        return this.targetMethodSignature.getProxyObject();
    }

    /**
     * Returns the Lucky AOP target. May be {@code null} if there is no target.
     */
    @Override
    @Nullable
    public Object getTarget() {
        return this.targetObject;
    }

    @Override
    public Object[] getArgs() {
        if (this.args == null) {
            this.args = targetMethodSignature.getParams();
        }
        return this.args;
    }

    @Override
    public Signature getSignature() {
        if (this.signature == null) {
            this.signature = new MethodSignatureImpl();
        }
        return this.signature;
    }

    @Override
    public SourceLocation getSourceLocation() {
        if (this.sourceLocation == null) {
            this.sourceLocation = new SourceLocationImpl();
        }
        return this.sourceLocation;
    }

    @Override
    public String getKind() {
        return ProceedingJoinPoint.METHOD_EXECUTION;
    }

    @Override
    public int getId() {
        // TODO: It's just an adapter but returning 0 might still have side effects...
        return 0;
    }

    @Override
    public JoinPoint.StaticPart getStaticPart() {
        return this;
    }

    @Override
    public String toShortString() {
        return "execution(" + getSignature().toShortString() + ")";
    }

    @Override
    public String toLongString() {
        return "execution(" + getSignature().toLongString() + ")";
    }

    @Override
    public String toString() {
        return "execution(" + getSignature().toString() + ")";
    }


    /**
     * Lazily initialized MethodSignature.
     */
    private class MethodSignatureImpl implements MethodSignature {

        @Nullable
        private volatile String[] parameterNames;

        @Override
        public String getName() {
            return targetMethodSignature.getCurrMethod().getName();
        }

        @Override
        public int getModifiers() {
            return targetMethodSignature.getCurrMethod().getModifiers();
        }

        @Override
        public Class<?> getDeclaringType() {
            return targetMethodSignature.getCurrMethod().getDeclaringClass();
        }

        @Override
        public String getDeclaringTypeName() {
            return targetMethodSignature.getCurrMethod().getDeclaringClass().getName();
        }

        @Override
        public Class<?> getReturnType() {
            return targetMethodSignature.getCurrMethod().getReturnType();
        }

        @Override
        public Method getMethod() {
            return targetMethodSignature.getCurrMethod();
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return targetMethodSignature.getCurrMethod().getParameterTypes();
        }

        @Override
        @Nullable
        public String[] getParameterNames() {
            String[] parameterNames = this.parameterNames;
            if (parameterNames == null) {
                parameterNames =targetMethodSignature.getNameMap().keySet().toArray(new String[0]);
                this.parameterNames = parameterNames;
            }
            return parameterNames;
        }

        @Override
        public Class<?>[] getExceptionTypes() {
            return targetMethodSignature.getCurrMethod().getExceptionTypes();
        }

        @Override
        public String toShortString() {
            return toString(false, false, false, false);
        }

        @Override
        public String toLongString() {
            return toString(true, true, true, true);
        }

        @Override
        public String toString() {
            return toString(false, true, false, true);
        }

        private String toString(boolean includeModifier, boolean includeReturnTypeAndArgs,
                                boolean useLongReturnAndArgumentTypeName, boolean useLongTypeName) {

            StringBuilder sb = new StringBuilder();
            if (includeModifier) {
                sb.append(Modifier.toString(getModifiers()));
                sb.append(" ");
            }
            if (includeReturnTypeAndArgs) {
                appendType(sb, getReturnType(), useLongReturnAndArgumentTypeName);
                sb.append(" ");
            }
            appendType(sb, getDeclaringType(), useLongTypeName);
            sb.append(".");
            sb.append(getMethod().getName());
            sb.append("(");
            Class<?>[] parametersTypes = getParameterTypes();
            appendTypes(sb, parametersTypes, includeReturnTypeAndArgs, useLongReturnAndArgumentTypeName);
            sb.append(")");
            return sb.toString();
        }

        private void appendTypes(StringBuilder sb, Class<?>[] types, boolean includeArgs,
                                 boolean useLongReturnAndArgumentTypeName) {

            if (includeArgs) {
                for (int size = types.length, i = 0; i < size; i++) {
                    appendType(sb, types[i], useLongReturnAndArgumentTypeName);
                    if (i < size - 1) {
                        sb.append(",");
                    }
                }
            }
            else {
                if (types.length != 0) {
                    sb.append("..");
                }
            }
        }

        private void appendType(StringBuilder sb, Class<?> type, boolean useLongTypeName) {
            if (type.isArray()) {
                appendType(sb, type.getComponentType(), useLongTypeName);
                sb.append("[]");
            }
            else {
                sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
            }
        }
    }


    /**
     * Lazily initialized SourceLocation.
     */
    private class SourceLocationImpl implements SourceLocation {

        @Override
        public Class<?> getWithinType() {
            if (targetMethodSignature.getProxyObject() == null) {
                throw new UnsupportedOperationException("No source location joinpoint available: target is null");
            }
            return targetMethodSignature.getProxyObject().getClass();
        }

        @Override
        public String getFileName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLine() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int getColumn() {
            throw new UnsupportedOperationException();
        }
    }

}
