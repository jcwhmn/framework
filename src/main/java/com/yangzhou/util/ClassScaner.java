package com.yangzhou.util;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 类扫描器（获取指定包下的指定类型）
 * 
 * @author lisuo
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ClassScaner {

	private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

	private final List<TypeFilter> includeFilters = new ArrayList<>();

	private final List<TypeFilter> excludeFilters = new ArrayList<>();

	private final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

	/**
	 * 添加包含的Fiter
	 * 
	 * @param includeFilter
	 */
	public void addIncludeFilter(TypeFilter includeFilter) {
		includeFilters.add(includeFilter);
	}

	/**
	 * 添加排除的Fiter
	 * 
	 * @param includeFilter
	 */
	public void addExcludeFilter(TypeFilter excludeFilter) {
		excludeFilters.add(excludeFilter);
	}

	/**
	 * 扫描指定的包，获取包下所有的Class
	 * 
	 * @param basePackage 包名
	 * @param targetTypes 需要指定的目标类型,可以是pojo,可以是注解
	 * @return Set<Class<?>>
	 */
	public static Set<Class<?>> scan(String basePackage, Class<?>... targetTypes) {
		log.debug("Class scan package : {}, targeTypes : {}", basePackage, targetTypes[0]);
		final ClassScaner cs = new ClassScaner();
		for (final Class<?> targetType : targetTypes) {
			if (TypeUtils.isAssignable(Annotation.class, targetType)) {
				log.debug("{} is assignable", targetType.getName());
				cs.addIncludeFilter(new AnnotationTypeFilter((Class<? extends Annotation>) targetType));
			} else {
				log.debug("{} is not assignable", targetType.getName());
				cs.addIncludeFilter(new AssignableTypeFilter(targetType));
			}
		}
		return cs.doScan(basePackage);
	}

	/**
	 * 扫描指定的包，获取包下所有的Class
	 * 
	 * @param basePackages 包名,多个
	 * @param targetTypes  需要指定的目标类型,可以是pojo,可以是注解
	 * @return Set<Class<?>>
	 */
	public static Set<Class<?>> scan(String[] basePackages, Class<?>... targetTypes) {
		final ClassScaner cs = new ClassScaner();
		for (final Class<?> targetType : targetTypes) {
			if (TypeUtils.isAssignable(Annotation.class, targetType)) {
				cs.addIncludeFilter(new AnnotationTypeFilter((Class<? extends Annotation>) targetType));
			} else {
				cs.addIncludeFilter(new AssignableTypeFilter(targetType));
			}
		}
		final Set<Class<?>> classes = new HashSet<>();
		for (final String s : basePackages) {
			classes.addAll(cs.doScan(s));
		}
		return classes;
	}

	/**
	 * 扫描指定的包，获取包下所有的Class
	 * 
	 * @param basePackages 包名
	 * @return Set<Class<?>>
	 */
	public Set<Class<?>> doScan(String[] basePackages) {
		final Set<Class<?>> classes = new HashSet<>();
		for (final String basePackage : basePackages) {
			classes.addAll(doScan(basePackage));
		}
		return classes;
	}

	/**
	 * 扫描指定的包，获取包下所有的Class
	 * 
	 * @param basePackages 包名
	 * @return Set<Class<?>>
	 */
	public Set<Class<?>> doScan(String basePackage) {
		final Set<Class<?>> classes = new HashSet<>();
		try {
			final String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage))
					+ "/**/*.class";
			final Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			for (final Resource resource : resources) {
				if (resource.isReadable()) {
					final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
					if (includeFilters.size() == 0 && excludeFilters.size() == 0 || matches(metadataReader)) {
						try {
							log.debug("获取类： {}", metadataReader.getClassMetadata().getClassName());
							classes.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
						} catch (final ClassNotFoundException ignore) {
						}
					}
				}
			}
		} catch (final IOException ex) {
			throw new RuntimeException("I/O failure during classpath scanning", ex);
		}
		return classes;
	}

	/**
	 * 处理 excludeFilters和includeFilters
	 * 
	 * @param metadataReader
	 * @return boolean
	 * @throws IOException
	 */
	private boolean matches(MetadataReader metadataReader) throws IOException {
		for (final TypeFilter tf : excludeFilters) {
			if (tf.match(metadataReader, metadataReaderFactory)) return false;
		}
		for (final TypeFilter tf : includeFilters) {
			if (tf.match(metadataReader, metadataReaderFactory)) return true;
		}
		return false;
	}
}