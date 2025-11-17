package au.org.ala.bootstrap5

import grails.config.Config
import grails.testing.services.ServiceUnitTest
import org.grails.encoder.CodecLookup
import org.grails.encoder.Encoder
import org.grails.web.mapping.DefaultLinkGenerator
import org.grails.web.mapping.UrlMappingsHolderFactoryBean
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockServletContext
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class TagLinkServiceSpec extends Specification implements ServiceUnitTest<TagLinkService> {

    String logoutRequestUri
    HttpServletRequest logoutRequest

    def serverUrl = "http://bie.ala.org.au"

    @Override
    Closure doWithConfig() { return { Config config ->
        config.grails.serverURL = serverUrl
    }}

    @Override
    Closure doWithSpring() {return { ->
        grailsUrlMappingsHolder(UrlMappingsHolderFactoryBean)
        grailsLinkGenerator(DefaultLinkGenerator, serverUrl, '')
    }}

    def setup() {
        def servletContext = new MockServletContext()
        logoutRequestUri = service.grailServerURL+'/some/path/with/params?test&foo=bar'
        logoutRequest = MockMvcRequestBuilders.request(HttpMethod.GET, logoutRequestUri).buildRequest(servletContext)
        Encoder noopEncoder = Stub(Encoder) {
            encode(_) >> { obj -> obj }
            isSafe() >> true
        }

        service.codecLookup = Stub(CodecLookup) {
            lookupEncoder('HTML') >> noopEncoder
        }
    }

    def cleanup() {
    }

    void "test clearing cache"() {
        when:
        service.clearCache()

        then:
        service.hfCache.every { it.value.content == '' }
    }

    void "test build default logout URL with no additional parameters"() {
        setup:

        expect:
        service.buildLogoutUrl(logoutRequest) == "${service.grailServerURL}/"
    }

    void "test build logout URL using a defined logout URL"() {
        setup:
        def request = new MockHttpServletRequestBuilder(HttpMethod.GET, 'http://example.com').buildRequest(new MockServletContext())
        def logoutUrlBase = service.grailServerURL + '/logout'

        expect:
        service.buildLogoutUrl(request, null, logoutUrlBase, null) == "${service.grailServerURL}/logout"
    }

    void "test build logout URL excludes CAS logout URL by default"() {
        setup:
        def logoutUrlBase = service.grailServerURL + '/logout'
        def casLogoutUrl = service.casLogoutUrl

        expect:
        service.buildLogoutUrl(logoutRequest, casLogoutUrl, logoutUrlBase, null) == "${service.grailServerURL}/logout"
    }

    void "test build logout URL excludes Default App return URL by default"() {
        setup:
        def logoutUrlBase = service.grailServerURL + '/logout'
        def casLogoutUrl = service.casLogoutUrl

        expect:
        service.buildLogoutUrl(logoutRequest, casLogoutUrl, logoutUrlBase, null) == "${service.grailServerURL}/logout"
    }

    void "test build logout URL includes Custom App return URL"() {
        setup:
        def logoutUrlBase = service.grailServerURL + '/logout'
        def casLogoutUrl = service.casLogoutUrl
        def returnUrl = "http://example.com/bye"

        expect:
        service.buildLogoutUrl(logoutRequest, casLogoutUrl, logoutUrlBase, returnUrl) == "${service.grailServerURL}/logout?appUrl=$returnUrl"

    }

    void "mustache renders simple variables"() {
        given:
        String template = "<div>{{containerClass}}</div>"

        and:
        Map model = [ containerClass: "container-fluid" ]

        when:
        String result = service.render("banner", template, model)

        then:
        result == "<div>container-fluid</div>"
    }

    void "transform() renders template with mustache variables"() {
        given:
        service.metaClass.getContent = { String which ->
            return """
            <div class="{{containerClass}}">{{centralServer}}</div>
        """
        }

        and:
        def request = Mock(HttpServletRequest)
        Map attrs = [:]
        grailsApplication.config.skin.layout = "ala-site-main"

        service.metaClass.isLoggedIn = { req, a -> false }

        when:
        String result = service.transform("banner", request, service.getContent("banner"), attrs)

        then:
        result.contains("class=\"container-fluid\"")
        result.contains(service.alaBaseURL)
    }

    void "transform() renders old template with variables"() {
        given:
        service.metaClass.getContent = { String which ->
            return """
            <div class="::containerClass::">::centralServer::</div>
        """
        }

        and:
        def request = Mock(HttpServletRequest)
        Map attrs = [:]
        grailsApplication.config.skin.layout = "ala-main"

        service.metaClass.isLoggedIn = { req, a -> false }

        when:
        String result = service.transform("banner", request, service.getContent("banner"), attrs)

        then:
        result.contains("class=\"container-fluid\"")
        result.contains(service.alaBaseURL)
    }

}
