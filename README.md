# splitul

API de gestion des dépenses partagées et des dettes entre membres d’un groupe.

**Java 21 · Jersey · Maven · JUnit · Mockito**

Copie portfolio d’un projet scolaire de Juan José Castilla Manrique ([OneCosmicDev](https://github.com/OneCosmicDev)). Les contributions de l’équipe et le matériel fourni par le cours sont crédités ci-dessous.

## Ma contribution — Juan José Castilla Manrique

J'ai contribué à la répartition des dépenses et des dettes, aux contrôles d'accès de l'API et à la vérification des dépendances.

- **Répartition des dettes entre membres** : implémentation d'un algorithme glouton convertissant les soldes nets en dettes entre paires de membres, dans la couche de conversion vers les DTO. [PR #36](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/36).
- **Historique des dépenses** : ajout de l'endpoint de consultation, de son DTO, du tri et des contrôles d'appartenance au groupe, avec des tests. [PR #59](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/59).
- **Filtrage des accès** : centralisation des contrôles utilisant l'en-tête de membre et correction de la suppression des groupes, avec adaptation des tests. [PR #60](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/60).
- **Stratégies de partage** : implémentation de stratégies de répartition, de pourcentages personnalisés et de leur validation, avec les tests associés. [PR #81](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/81).
- **Analyse des dépendances** : intégration d'OWASP Dependency-Check dans Maven et le workflow CI. [PR #84](https://github.com/GLO-2003-eq17/GLO-2003-SplitUL/pull/84).

## Équipe et crédits

L'équipe indiquée dans le README source réunit Victoria Pelletier Cantin, Yan Tremblay, Dania Mahfoud, Alissa Audet et Juan José Castilla Manrique.

Comptes contributeurs identifiés : [VictoriaaPc](https://github.com/VictoriaaPc), [Yurhuval](https://github.com/Yurhuval), [mhfdania](https://github.com/mhfdania), [AlissaAudet](https://github.com/AlissaAudet) et [OneCosmicDev](https://github.com/OneCosmicDev).

Le projet s'inscrit dans le cadre pédagogique du cours GLO-2003. Le fichier LICENSE et les mentions d'origine du dépôt doivent être conservés lors de sa redistribution. Les contributions décrites ci-dessus ne m'attribuent pas l'ensemble de l'API ni les contributions des autres membres.


Les références de PR et de commits pointent vers les dépôts pédagogiques d’origine, dont l’accès peut être restreint. La présente copie possède son propre historique de publication.

## Démarrer le projet

Prérequis : Java 21 et Maven 3.

```sh
mvn compile
mvn exec:java
```

L’API écoute par défaut sur `http://localhost:8080` et utilise un stockage en mémoire. La persistance MongoDB est optionnelle. `SENTRY_DSN` peut rester absent pour une exécution locale.

```sh
mvn test
```

Le projet conserve ses outils Maven de qualité issus du cours. Les analyses de dépendances et les tests nécessitant un service externe peuvent demander une configuration supplémentaire.

## État de cette publication

Cette version présente le travail scolaire et ses limites. Elle ne correspond pas à un service hébergé ni à un engagement de maintenance. Voir [PROVENANCE.md](PROVENANCE.md) pour la source, les adaptations de publication et les références vers le code.

## Vérifications du 6 octobre 2026

Compilation et 136 tests réussis. Les 9 tests MongoDB restants ne peuvent pas démarrer sans environnement Docker disponible pour Testcontainers ; la suite complète n’est donc pas déclarée verte.
