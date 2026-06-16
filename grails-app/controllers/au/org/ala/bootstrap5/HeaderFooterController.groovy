package au.org.ala.bootstrap5

/**
 * Controller to clear header/footer cache
 */
class HeaderFooterController {

    /**
     * Clear the cache - calls Taglib
     * Usage: http://serverUrl/headerFooter/clearCache
     */
    def clearCache() {
        def msg = hf.clearCache()
        render(status: 200, text: "${msg}")
    }
}
