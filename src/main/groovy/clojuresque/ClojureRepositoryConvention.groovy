/*-
 * Copyright 2009-2015 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Licensed under the EUPL V.1.1 (cf. file EUPL-1.1 distributed with the
 * source code.) Translations in other european languages available at
 * https://joinup.ec.europa.eu/software/page/eupl.
 *
 * Alternatively, you may choose to use the software under the MIT license
 * (cf. file MIT distributed with the source code).
 */

package clojuresque

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class ClojureRepositoryConvention {
    private RepositoryHandler repos

    public ClojureRepositoryConvention(RepositoryHandler repos) {
        this.repos = repos
    }

    public void clojarsRepo() {
        repos.maven { url 'http://clojars.org/repo' }
    }
}
