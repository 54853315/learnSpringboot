package pers.learn.framework.shiro.session;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 重写 DefaultWebSubjectFactory 中的createSubject以便禁用Session
 * 以后调用Subject.getSession()将抛出DisabledSessionException异常，所以请注意之后不要用session啦
 * 如果securityManager使用DefaultWebSubjectFactory则会在登录成功后创建session，报DisabledSessionException异常：Session creation has been disabled for the current subject.  This exception indicates that there is either a programming error (using a session when it should never be used) or that Shiro's configuration needs to be adjusted to allow Sessions to be created for the current Subject.  See the org.apache.shiro.subject.support.DisabledSessionException JavaDoc for more.
 */
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {
    @Override
    public Subject createSubject(SubjectContext context) {
        //不创建session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}
