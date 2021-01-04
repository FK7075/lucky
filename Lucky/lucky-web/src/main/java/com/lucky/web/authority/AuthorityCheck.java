package com.lucky.web.authority;

import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.authority.annotation.MustGuest;
import com.lucky.web.authority.annotation.MustPermissions;
import com.lucky.web.authority.annotation.MustRoles;
import com.lucky.web.authority.annotation.MustUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 权限校验器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午9:13
 */
public class AuthorityCheck {
    private static final Logger log= LoggerFactory.getLogger(AuthorityCheck.class);
    private static final UserInformation userInformation=UserInformationManage.create().getUserInformation();
    private static final Class<? extends Annotation>[] AUTHORITY_ANNOTATION
            =new Class[]{MustGuest.class, MustRoles.class, MustPermissions.class, MustUser.class};

    private Role role=new Role();
    private Permissions permissions=new Permissions();
    private boolean isGuest;
    private boolean isUser;
    private boolean isAuthorityMethod;
    private Method controllerMethod;

    /**
     * 初始化
     * @param controllerClass Controller类的Class
     * @param controllerMethod Controller类的UrlMapping方法
     */
    public AuthorityCheck(Class<?> controllerClass,Method controllerMethod){
        this.controllerMethod=controllerMethod;
        controllerClass= CglibProxy.isAgent(controllerClass)?controllerClass.getSuperclass():controllerClass;
        isAuthorityMethod= AnnotationUtils.isExistOrByArray(controllerClass,AUTHORITY_ANNOTATION)
                         ||AnnotationUtils.isExistOrByArray(controllerMethod,AUTHORITY_ANNOTATION);
        if(isAuthorityMethod){
            if(isAnnotation(controllerClass,controllerMethod,MustGuest.class)){
                isGuest=true;
            }
            if(isAnnotation(controllerClass,controllerMethod,MustUser.class)){
                isUser=true;
            }
            if(isAnnotation(controllerClass,controllerMethod,MustRoles.class)){
                MustRoles mustRoles=AnnotationUtils.isExist(controllerMethod,MustRoles.class)?
                        AnnotationUtils.get(controllerMethod,MustRoles.class):AnnotationUtils.get(controllerClass,MustRoles.class);
                role=new Role(mustRoles.value(),mustRoles.logical());
                isUser=true;
            }
            if(isAnnotation(controllerClass,controllerMethod,MustPermissions.class)){
                MustPermissions mustPermissions=AnnotationUtils.isExist(controllerMethod,MustPermissions.class)?
                        AnnotationUtils.get(controllerMethod,MustPermissions.class):AnnotationUtils.get(controllerClass,MustPermissions.class);
                permissions=new Permissions(mustPermissions.value(),mustPermissions.logical());
                isUser=true;
            }
        }
        if(isGuest&&isUser){
            throw new AuthorityInitializeException(controllerMethod);
        }

    }


    /**
     * 权限校验
     * @return 校验通过返回true，否则返回false
     */
    public boolean check(){
        //该资源没有配置权限管理，返回true
        if(!isAuthorityMethod){
            return true;
        }
        //没有设置UserInformation,既没有开启权限验证，返回true
        if(userInformation==null){
            return true;
        }
        //当前用户没有通过验证,如果资源上被@MustGuest标注则返回true，否则返回false
        if(!userInformation.isAuthenticated()){
            if(!isGuest){
                log.info("`游客`无法访问`用户`的专属资源（{}）",controllerMethod);
            }
           return isGuest;
        }

        /* V-当前用户已经通过验证-V */

        //如果资源上被@MustGuest标注,返回false
        if(isGuest){
            log.info("`用户`无法访问`游客`的专属资源（{}）",controllerMethod);
            return false;
        }
        //权限与资源验证
        final RoleAndPermissions roleAndPermissions = userInformation.roleAndPermissions();
        boolean roleCheck = role.check(roleAndPermissions.getRoles());
        boolean permissionCheck = permissions.check(roleAndPermissions.getPermissions());
        if(!roleCheck){
            log.info("角色检验失败！用户`{}`所属的角色 {} 没有资源（{}）的访问权限",userInformation.getUser(),roleAndPermissions.getRoles(),controllerMethod);
        }
        if(!permissionCheck){
            log.info("权限检验失败！用户`{}`权限 {} 不足，无法访问资源（{}）",userInformation.getUser(),roleAndPermissions.getPermissions(),controllerMethod);
        }
        return roleCheck&&permissionCheck;
    }

    private boolean isAnnotation(Class<?> controllerClass,Method controllerMethod,Class<? extends Annotation> annotationClass){
        return AnnotationUtils.isExist(controllerClass,annotationClass)
                ||AnnotationUtils.isExist(controllerMethod,annotationClass);
    }

}
