import React, { useMemo } from 'react';

/**
 * Strip whitespace, quotes & trailing brackets.
 */
export function sanitizeSvgBase64(rawBase64 = "") {
  return rawBase64
    .replace(/\s+/g, "")        // drop ALL spaces/newlines
    .trim()
    .replace(/^"+|"+$/g, "")    // strip wrapping quotes
    .replace(/\]+$/g, "");      // strip trailing brackets
}

/**
 * Guarantee your <svg> tag has width & height attributes,
 * by decoding, patching the <svg> element, and re-encoding.
 *
 * @param rawBase64  The incoming chartSvg string
 * @param w          Desired width (e.g. "600")
 * @param h          Desired height (e.g. "600")
 */
export function withDimensions(rawBase64, w = 600, h = 600) {
  const clean = sanitizeSvgBase64(rawBase64);
  let decoded;
  try {
    decoded = atob(clean.trim());
  } catch {
    return clean;   // fallback to sanitized only
  }
  // replace the opening <svg …> with one that has width/height
  const patched = decoded.replace(
    /^<svg\b([^>]*)>/,
    `<svg width="${w}" height="${h}"$1>`
  );
  return btoa(patched);
}



export function base64Decode(base64Svg = "") {
  if (!base64Svg?.trim()) return null;
    try {
        //console.log("What BASE64 I get: ",base64Svg)    
        //const base64Data = base64Svg.replace(/^data:image\/svg\+xml;base64,/, '');
        // console.log("What BASE64 I Strip and try to decode: ",base64Data)    
        const decoded = atob(base64Svg);
        // console.log("What I return: ",decoded)    
        return decoded
  } catch (error) {
    console.error("SVG decoding failed:", base64Svg);
    return null;
  }
}

export function decodeb64Svg(base64) {
  // 1) Strip any data‑URI prefix
  const prefix = "data:image/svg+xml;base64,";
  let str = base64.startsWith(prefix)
    ? base64.slice(prefix.length)
    : base64;

  // 2) Remove all whitespace/newlines
  str = str.replace(/\s+/g, "");

  // 3) Convert URL‑safe chars back to “classic” Base64
  str = str.replace(/-/g, "+").replace(/_/g, "/");

  // 4) Pad with “=” if necessary so length % 4 === 0
  while (str.length % 4 !== 0) {
    str += "=";
  }

  // 5) Finally decode
  return window.atob(str);
}



export function AstrologyResultMapImg({ report }) {
  return (
    <div
      className="chart-container"
      // Render the patched SVG inline
      dangerouslySetInnerHTML={{ __html: report }}
    />
  );
}

