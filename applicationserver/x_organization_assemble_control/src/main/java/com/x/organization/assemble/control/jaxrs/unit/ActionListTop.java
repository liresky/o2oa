package com.x.organization.assemble.control.jaxrs.unit;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

import net.sf.ehcache.Element;

class ActionListTop extends BaseAction {
	/** 用于显示顶层组织,输出下级组织和组织成员数量 */
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass());
			Element element = business.cache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				List<Wo> wos = this.list(business);
				business.cache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			this.updateControl(effectivePerson, business, result.getData());
			return result;
		}
	}

	public static class Wo extends WoAbstractUnit {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("直接下级组织数量")
		private Long subDirectUnitCount = 0L;

		@FieldDescribe("直接成员身份数量")
		private Long subDirectIdentityCount = 0L;

		static WrapCopier<Unit, Wo> copier = WrapCopierFactory.wo(Unit.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		public Long getSubDirectUnitCount() {
			return subDirectUnitCount;
		}

		public void setSubDirectUnitCount(Long subDirectUnitCount) {
			this.subDirectUnitCount = subDirectUnitCount;
		}

		public Long getSubDirectIdentityCount() {
			return subDirectIdentityCount;
		}

		public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
			this.subDirectIdentityCount = subDirectIdentityCount;
		}

	}

	private List<Wo> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.level), 1);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		List<Wo> wos = Wo.copier.copy(os);
		wos.stream().forEach(o -> {
			try {
				o.setSubDirectUnitCount(this.countSubDirectUnit(business, o));
				o.setSubDirectIdentityCount(this.countSubDirectIdentity(business, o));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		wos = business.unit().sort(wos);
		return wos;
	}

	private Long countSubDirectUnit(Business business, Wo wo) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.superior), wo.getId());
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		return count;
	}

	private Long countSubDirectIdentity(Business business, Wo wo) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), wo.getId());
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		return count;
	}
}