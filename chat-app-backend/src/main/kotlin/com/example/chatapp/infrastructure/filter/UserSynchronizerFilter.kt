package com.example.chatapp.infrastructure.filter

import com.example.chatapp.infrastructure.security.UserSynchronizer
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter

@Service
class UserSynchronizerFilter(
    private val userSynchronizer: UserSynchronizer
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if (SecurityContextHolder.getContext().authentication !is AnonymousAuthenticationToken) {
            val token = (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken)

            userSynchronizer.synchronizeWithIdp(token.token)
        }

        filterChain.doFilter(request, response)
    }
}