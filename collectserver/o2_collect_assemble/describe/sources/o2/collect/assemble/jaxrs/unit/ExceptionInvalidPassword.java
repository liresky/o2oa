package o2.collect.assemble.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidPassword extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionInvalidPassword() {
		super("密码过于简单.");
	}
}
