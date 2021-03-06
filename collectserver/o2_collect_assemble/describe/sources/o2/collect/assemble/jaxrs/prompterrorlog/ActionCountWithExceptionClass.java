package o2.collect.assemble.jaxrs.prompterrorlog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.log.PromptErrorLog;
import o2.collect.core.entity.log.PromptErrorLog_;

class ActionCountWithExceptionClass extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			for (String exceptionClass : this.listExceptionClass(business)) {
				Wo wo = new Wo();
				wo.setName(exceptionClass);
				wo.setValue(this.count(business, exceptionClass));
				wos.add(wo);
			}
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getCount, Comparator.nullsLast(Long::compareTo)).reversed())
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	private List<String> listExceptionClass(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PromptErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PromptErrorLog> root = cq.from(PromptErrorLog.class);
		cq.select(root.get(PromptErrorLog_.exceptionClass));
		return em.createQuery(cq.distinct(true)).getResultList();
	}

	private Long count(Business business, String exceptionClass) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PromptErrorLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PromptErrorLog> root = cq.from(PromptErrorLog.class);
		Predicate p = cb.equal(root.get(PromptErrorLog_.exceptionClass), exceptionClass);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public static class Wo extends NameValueCountPair {

	}

}
