package com.x.face.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.PromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyEditUnit extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyEditUnit(EffectivePerson effectivePerson, String name) {
		super("{} 不能编辑组织:{}, 权限不足.", effectivePerson.getDistinguishedName(), name);
	}
}
