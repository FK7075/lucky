package com.lucky.shiro.aspect;

import com.lucky.aop.core.AopChain;
import com.lucky.aop.core.InjectionAopPoint;
import com.lucky.aop.core.TargetMethodSignature;
import com.lucky.utils.reflect.AnnotationUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.subject.Subject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限扩展
 * @author DELL
 *
 */
public class LuckyShiroAccessControlPoint extends InjectionAopPoint {

    public static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES =
            new Class[] {
                    RequiresPermissions.class, RequiresRoles.class,
                    RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class
            };

    public LuckyShiroAccessControlPoint(){
        setPriority(-1);
    }

    @Override
    public Object proceed(AopChain chain) throws Throwable {
        TargetMethodSignature targetMethodSignature = tlTargetMethodSignature.get();
        permissionCheck(targetMethodSignature.getTargetClass(),targetMethodSignature.getCurrMethod());
        return chain.proceed();
    }

    private void permissionCheck(Class<?> currClass,Method currMethod){
        List<Annotation> anns =getAnnotations(currClass,currMethod);
        for (Annotation ann : anns) {
            rolesPermissionCheck(ann);
            permissionsPermissionCheck(ann);
            authenticationPermissionCheck(ann);
            UserPermissionCheck(ann);
            guestPermissionCheck(ann);
        }
    }

    private void rolesPermissionCheck(Annotation a){
        if (!(a instanceof RequiresRoles)) return;
        RequiresRoles rrAnnotation = (RequiresRoles) a;
        String[] roles = rrAnnotation.value();

        if (roles.length == 1) {
            getSubject().checkRole(roles[0]);
            return ;
        }
        if (Logical.AND.equals(rrAnnotation.logical())) {
            getSubject().checkRoles(Arrays.asList(roles));
            return ;
        }
        if (Logical.OR.equals(rrAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOneRole = false;
            for (String role : roles) if (getSubject().hasRole(role)) hasAtLeastOneRole = true;
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOneRole) getSubject().checkRole(roles[0]);
            return ;
        }
    }

    private void permissionsPermissionCheck(Annotation a){
        if (!(a instanceof RequiresPermissions)) return;
        RequiresPermissions rpAnnotation = (RequiresPermissions) a;
        String[] perms = rpAnnotation.value();
        Subject subject = getSubject();

        if (perms.length == 1) {
            subject.checkPermission(perms[0]);
            return ;
        }
        if (Logical.AND.equals(rpAnnotation.logical())) {
            getSubject().checkPermissions(perms);
            return ;
        }
        if (Logical.OR.equals(rpAnnotation.logical())) {
            // Avoid processing exceptions unnecessarily - "delay" throwing the exception by calling hasRole first
            boolean hasAtLeastOnePermission = false;
            for (String permission : perms) if (getSubject().isPermitted(permission)) hasAtLeastOnePermission = true;
            // Cause the exception if none of the role match, note that the exception message will be a bit misleading
            if (!hasAtLeastOnePermission) getSubject().checkPermission(perms[0]);
        }
    }

    private void authenticationPermissionCheck(Annotation a){
        if( a instanceof RequiresAuthentication &&!getSubject().isAuthenticated()){
            throw new UnauthenticatedException( "The current Subject is not authenticated.  Access denied." );
        }
    }

    private void UserPermissionCheck(Annotation a){
        if(a instanceof RequiresUser && getSubject().getPrincipal() == null){
            throw new UnauthenticatedException("Attempting to perform a user-only operation.  The current Subject is " +
                    "not a user (they haven't been authenticated or remembered from a previous login).  " +
                    "Access denied.");
        }
    }

    private void guestPermissionCheck(Annotation a){
        if(a instanceof RequiresGuest && getSubject().getPrincipal() != null){
            throw new UnauthenticatedException("Attempting to perform a guest-only operation.  The current Subject is " +
                    "not a guest (they have been authenticated or remembered from a previous login).  Access " +
                    "denied.");
        }
    }

    private Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    private List<Annotation> getAnnotations(Class<?> currClass, Method currMethod) {
        List<Annotation> anns=new ArrayList<>(5);
        for (Class<? extends Annotation> ac : AUTHZ_ANNOTATION_CLASSES) {
            boolean chave= AnnotationUtils.isExist(currClass,ac);
            boolean mhave=AnnotationUtils.isExist(currMethod,ac);
            if(mhave){
                anns.add(AnnotationUtils.get(currMethod,ac));
                continue;
            }
            if(chave){
                anns.add(AnnotationUtils.get(currClass,ac));
                continue;
            }
        }
        return anns;
    }

    @Override
    public boolean pointCutMethod(Class<?> currClass, Method currMethod) {
        return AnnotationUtils.isExistOrByArray(currClass, LuckyShiroAccessControlPoint.AUTHZ_ANNOTATION_CLASSES)||
				AnnotationUtils.isExistOrByArray(currMethod, LuckyShiroAccessControlPoint.AUTHZ_ANNOTATION_CLASSES);
    }

    @Override
    public boolean pointCutClass(Class<?> currClass) {
        if (AnnotationUtils.isExistOrByArray(currClass,AUTHZ_ANNOTATION_CLASSES)) {
            return true;
        }
        Method[] declaredMethods = currClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (AnnotationUtils.isExistOrByArray(method,AUTHZ_ANNOTATION_CLASSES)) {
                return true;
            }
        }
        return false;
    }
}
