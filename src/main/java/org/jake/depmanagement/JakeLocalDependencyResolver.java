package org.jake.depmanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jake.JakeDir;
import org.jake.utils.JakeUtilsIterable;

public final class JakeLocalDependencyResolver extends JakeDependencyResolver {

	private final HashMap<JakeScope, Iterable<File>> dependencies;

	private JakeLocalDependencyResolver(
			HashMap<JakeScope, Iterable<File>> dependencies) {
		super();
		this.dependencies = dependencies;
	}

	public static JakeLocalDependencyResolver empty() {
		return new JakeLocalDependencyResolver(new HashMap<JakeScope, Iterable<File>>());
	}

	public static JakeLocalDependencyResolver standard(File libDirectory) {
		final JakeDir libDir = JakeDir.of(libDirectory);
		return JakeLocalDependencyResolver.empty()
				.with(JakeScope.COMPILE, libDir.include("*.jar", "compile/*.jar"))
				.with(JakeScope.PROVIDED, libDir.include("provided/*.jar"))
				.with(JakeScope.RUNTIME, libDir.include("runtime/*.jar"))
				.with(JakeScope.TEST, libDir.include("test/*.jar"));
	}

	public static JakeLocalDependencyResolver standardIfExist(File libDirectory) {
		if (libDirectory.exists()) {
			return standard(libDirectory);
		}
		return empty();
	}

	@SuppressWarnings("unchecked")
	public JakeLocalDependencyResolver with(JakeScope scope, Iterable<File> files) {
		final List<File> newFiles = new LinkedList<File>();
		final Iterable<File> thisFiles = this.dependencies.get(scope);
		if (thisFiles != null) {
			for(final File file : thisFiles) {
				newFiles.add(file);
			}
		}
		for(final File file : files) {
			newFiles.add(file);
		}
		final HashMap<JakeScope, Iterable<File>> newMap =
				(HashMap<JakeScope, Iterable<File>>) this.dependencies.clone();
		newMap.put(scope, Collections.unmodifiableList(newFiles));
		return new JakeLocalDependencyResolver(newMap);
	}

	@Override
	public List<File> getDeclaredDependencies(JakeScope scope) {
		final Iterable<File> files = this.dependencies.get(scope);
		if (files == null) {
			return new ArrayList<File>(0);
		}
		return JakeUtilsIterable.toList(files);
	}

	@Override
	public Set<JakeScope> declaredScopes() {
		return Collections.unmodifiableSet(this.dependencies.keySet());
	}

}