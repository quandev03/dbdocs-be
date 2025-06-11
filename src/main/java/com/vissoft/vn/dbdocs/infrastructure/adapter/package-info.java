/**
 * This package contains infrastructure adapters that implement the ports defined in the application layer.
 * 
 * The adapter package follows the Hexagonal Architecture pattern and includes:
 * - Persistence adapters (implementing repository interfaces)
 * - External service adapters
 * - Security adapters
 * - Other technical implementations
 * 
 * Key concepts:
 * - Adapters implement interfaces (ports) defined in the application layer
 * - Adapters contain framework-specific code (Spring, JPA, etc.)
 * - Adapters translate between domain models and infrastructure-specific models
 * 
 * Package structure:
 * - persistence: Database-related adapters
 * - security: Security-related adapters
 * - service: External service adapters
 * 
 * @since 1.0
 */
package com.vissoft.vn.dbdocs.infrastructure.adapter;
